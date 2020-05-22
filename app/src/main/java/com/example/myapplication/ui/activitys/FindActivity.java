package com.example.myapplication.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CaseAdapter;
import com.example.myapplication.model.Case;
import com.example.myapplication.model.Page;

import com.example.myapplication.util.Contract;

import com.example.myapplication.util.GlobalHandler;
import com.example.myapplication.util.JsonUtils;
import com.example.myapplication.util.LoadingDailogUtil;

import com.example.myapplication.util.LogUtil;
import com.example.myapplication.util.OkHttpUtils;
import com.example.myapplication.util.RSAUtils;
import com.example.myapplication.util.ToastUtil;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class FindActivity extends AppCompatActivity implements GlobalHandler.HandleMsgListener{
    private TextView textViewTitle;
    private XRecyclerView mRecyclerView;
    private ArrayList<Case> mCases = new ArrayList<>();
    private SharedPreferences sp ;
    private SearchView searchView;
    //private View emptyView;
    private GlobalHandler mHandler;
    CaseAdapter adapter;
    private boolean isSearch = false;
    private boolean isCount = false;

    private ImageView back;
    String token;
    String userId;
    int page = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initView();
        getData(page,userId,token);
        setLister();

    }

    private void initView(){
        back = findViewById(R.id.iv_title);
        searchView = findViewById(R.id.searchView);
        textViewTitle = findViewById(R.id.tv_title);
        textViewTitle.setText("查找");
        mRecyclerView = findViewById(R.id.recyclerView);
        sp = getSharedPreferences("login", 0);
        token = sp.getString("token",null);
        userId = sp.getString("userId",null);
        mHandler = GlobalHandler.getInstance();
        mHandler.setHandleMsgListener(this);
        searchView.setSubmitButtonEnabled(true);
       // emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view,null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        mRecyclerView.setLayoutManager(layoutManager);
      //  mRecyclerView.setEmptyView(emptyView);
        mRecyclerView.getDefaultFootView().setNoMoreHint("————我是有底线的————");

        adapter = new CaseAdapter(mCases);
        mRecyclerView.setAdapter(adapter);




    }

    private void  setLister(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
           @Override
           public void onRefresh() {

           }

           @Override
           public void onLoadMore() {
              // LogUtil.d("下拉");
               if(isCount){

                   mRecyclerView.loadMoreComplete();
                   mRecyclerView.setNoMore(true);

               }else {

                   page++;
                   getData(page,userId,token);
               }
           }
       });

       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {
               isSearch = true;
               LoadingDailogUtil.showLoadingDialog(FindActivity.this,"搜索中...");
                getDataBySearch(query,userId,token);
               return false;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               if (!"".equals(searchView.getQuery().toString().trim())) {

                   mRecyclerView.setLoadingMoreEnabled(false);
               } else {

                   mRecyclerView.setLoadingMoreEnabled(true);
               }
               final ArrayList<Case> filteredModelList = filter(mCases, newText);
               adapter.setFilter(filteredModelList);
               return false;
           }
       });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                searchView.setQuery("", false);
                searchView.clearFocus();
                return true;
            }
        });

    }

    /**
     *
     * 逻辑4：过滤方法，目的是过滤符合当前数据中符合条件的数据源
     * @param models
     * @param query
     * @return
     */
    private ArrayList<Case> filter(ArrayList<Case> models, String query) {

        query = query.toLowerCase();

        final ArrayList<Case> filteredModelList = new ArrayList<>();

        for (Case model : models) {

            final String nameText = model.getOffName();
            final String idText = model.getOffCertificateNumber();
            final String carNumbertext = model.getOffPlateNumber();

            if (nameText.contains(query) || idText.contains(query) || carNumbertext.contains(query) ) {

                filteredModelList.add(model);

            }

        }

        return filteredModelList;

    }
    private void getData(int pageNumber,String userId,String token){
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        Page page = new Page(pageNumber,userId,token);
        String getDataJson = JsonUtils.conversionJsonString(page);
        try {
            String publicEncryptJson = RSAUtils.publicEncrypt(getDataJson,RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
            okHttpUtils.postInfo(Contract.SERVER_ADDRESS+"PoliceGetCasePage",publicEncryptJson);
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showShortToast("未知错误");
                    LoadingDailogUtil.cancelLoadingDailog();

                }

            });
        }


    }

    private void getDataBySearch(String query,String userId,String token){
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        Case case1 = new Case();
        case1.setOffName(query);
        case1.setPunishmentId(Integer.valueOf(userId));
        case1.setAppToken(token);

        String getDataJson = JsonUtils.conversionJsonString(case1);
        try {
            String publicEncryptJson = RSAUtils.publicEncrypt(getDataJson,RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
            okHttpUtils.postInfo(Contract.SERVER_ADDRESS+"GetSearchCaseServlet",publicEncryptJson);
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showShortToast("未知错误");
                    LoadingDailogUtil.cancelLoadingDailog();

                }

            });
        }


    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what){
            case 0:
                ToastUtil.showShortToast("网络出错，请检查网络");
                LoadingDailogUtil.cancelLoadingDailog();
                break;
            case 1:
                //也可以用这个接收
                try {
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("backInfo"));
                    switch (jsonObject.getString("code")){
                        case "ok":
                            if(!isSearch){
                                JSONArray jsonArray = jsonObject.getJSONArray("items");
                                isCount = jsonObject.getBoolean("isCount");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Case case1 = new Case();
                                    case1.setOffName(object.getString("offName"));
                                    case1.setOffCertificateNumber(object.getString("offCertificateNumber"));
                                    case1.setCaseId(object.getInt("caseId"));
                                    case1.setOffType(object.getString("offType"));
                                    case1.setOffTime(object.getString("offTime"));
                                    case1.setOffBirthPlace(object.getString("offBirthPlace"));
                                    case1.setOffPlace(object.getString("offPlace"));
                                    case1.setOffCertificateType(object.getString("offCertificateType"));
                                    case1.setOffPlateNumber(object.getString("offPlateNumber"));
                                    case1.setOffPunishmentType(object.getString("offPunishmentType"));
                                    case1.setPunishmentName(object.getString("punishmentName"));
                                    case1.setOffMoney(object.getInt("offMoney"));
                                    mCases.add(case1);

                                }
                                if(page == 0){
                                    //adapter = new CaseAdapter(mCases);
                                    // mRecyclerView.setAdapter(adapter);
                                    adapter.update(mCases);
                                }else {

                                    adapter.update(mCases);
                                    mRecyclerView.loadMoreComplete();
                                    ToastUtil.showShortToast("加载成功");
                                }
                            }else {
                                JSONArray jsonArray = jsonObject.getJSONArray("items");
                                ArrayList<Case> serachCases =new ArrayList<>();
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Case case1 = new Case();
                                    case1.setOffName(object.getString("offName"));
                                    case1.setOffCertificateNumber(object.getString("offCertificateNumber"));
                                    case1.setCaseId(object.getInt("caseId"));
                                    case1.setOffType(object.getString("offType"));
                                    case1.setOffTime(object.getString("offTime"));
                                    case1.setOffBirthPlace(object.getString("offBirthPlace"));
                                    case1.setOffPlace(object.getString("offPlace"));
                                    case1.setOffCertificateType(object.getString("offCertificateType"));
                                    case1.setOffPlateNumber(object.getString("offPlateNumber"));
                                    case1.setOffPunishmentType(object.getString("offPunishmentType"));
                                    case1.setPunishmentName(object.getString("punishmentName"));
                                    case1.setOffMoney(object.getInt("offMoney"));
                                    serachCases.add(case1);

                                }
                                isSearch = false;
                                adapter.update(serachCases);
                                LoadingDailogUtil.cancelLoadingDailog();
                            }


                            break;
                        case "003":

                            checkInvalidDialog();
                            if(isSearch){
                                isSearch = false;
                                LoadingDailogUtil.cancelLoadingDailog();
                            }
                            break;
                        default:
                            if(isSearch){
                                isSearch = false;
                                LoadingDailogUtil.cancelLoadingDailog();
                            }
                            ToastUtil.showShortToast("服务器错误");



                    }
                    break;
                }catch (Exception e){
                    e.printStackTrace();
                    if(isSearch){
                        isSearch = false;
                        LoadingDailogUtil.cancelLoadingDailog();
                    }
                    ToastUtil.showShortToast("未知错误");

                }

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁移除所有消息，避免内存泄露
        mHandler.removeCallbacks(null);
    }

    private void checkInvalidDialog(){
        final NormalDialog dialog = new NormalDialog(FindActivity.this);
        dialog.content("身份失效，请重新登录")
                .btnNum(1)
                .btnText("确定")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isTokenValid",false);
                editor.apply();
                Intent intent = new Intent(FindActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
