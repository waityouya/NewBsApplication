package com.example.myapplication.ui.fragments;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.example.myapplication.R;
import com.example.myapplication.adapter.AuxiliryCaseAdapter;

import com.example.myapplication.model.AuxiliryCase;

import com.example.myapplication.model.Page;
import com.example.myapplication.ui.activitys.AuditActivity;

import com.example.myapplication.ui.activitys.MainActivity;
import com.example.myapplication.util.Contract;
import com.example.myapplication.util.GlobalHandler;
import com.example.myapplication.util.GlobalHandler1;
import com.example.myapplication.util.JsonUtils;
import com.example.myapplication.util.LoadingDailogUtil;
import com.example.myapplication.util.LogUtil;
import com.example.myapplication.util.MyApplication;
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


public class PassFragment extends Fragment implements GlobalHandler1.HandleMsgListener{

    private XRecyclerView recyclerView;
    private GlobalHandler1 mHandler;
    private boolean isCount = false;
    private SharedPreferences sp ;
    String token;
    String userId;
    int page = 0;
    int auditType = 0;
    private AuxiliryCaseAdapter adapter;
    private ArrayList<AuxiliryCase> mCases = new ArrayList<>();

    public PassFragment(){

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        auditType = bundle.getInt("auditType");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg_content, container, false);
        initView(view);
        getData(page,userId,token,auditType);
        setLister();
        return view;
    }

    @Override
    public void onPause(){
        super.onPause();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁移除所有消息，避免内存泄露
        mHandler.removeCallbacks(null);
    }

    private void  initView(View view){
        sp = MyApplication.getContext().getSharedPreferences("login", 0);
        token = sp.getString("token",null);
        userId = sp.getString("userId",null);
        mHandler = GlobalHandler1.getInstance();
        mHandler.setHandleMsgListener(this);
        recyclerView = view.findViewById(R.id.audit_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AuditActivity.getAudiActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        recyclerView.getDefaultFootView().setNoMoreHint("————我是有底线的————");

        adapter = new AuxiliryCaseAdapter(mCases);
        recyclerView.setAdapter(adapter);
    }

    private void setLister(){
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                if(isCount){

                    recyclerView.loadMoreComplete();
                    recyclerView.setNoMore(true);

                }else {

                    page++;
                    getData(page,userId,token,auditType);
                }
            }
        });
    }

    private void getData(int pageNumber,String userId,String token,int auditType){
        LogUtil.d("pass"+auditType);
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        Page page = new Page(pageNumber,userId,token,auditType);
        String getDataJson = JsonUtils.conversionJsonString(page);
        try {
            String publicEncryptJson = RSAUtils.publicEncrypt(getDataJson,RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
            okHttpUtils.postInfo1(Contract.SERVER_ADDRESS+"GetAuxiliaryCaseServlet",publicEncryptJson);
        }catch (Exception e){
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
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
                            LogUtil.d("msg2shoudao");
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            isCount = jsonObject.getBoolean("isCount");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                AuxiliryCase case1 = new AuxiliryCase();
                                case1.setOffName(object.getString("offName"));
                                case1.setOffCertificateNumber(object.getString("offCertificateNumber"));
                                case1.setCaseId(object.getInt("caseId"));
                                case1.setOffType(object.getString("offType"));
                                case1.setOffTime(object.getString("offTime"));
                                case1.setAuditType(object.getInt("auditType"));
                                mCases.add(case1);

                            }
                            if(page == 0){
                                //adapter = new CaseAdapter(mCases);
                                // mRecyclerView.setAdapter(adapter);
                                LogUtil.d("page0");
                                adapter.update(mCases);
                                //AuditActivity.adapter.notifyDataSetChanged();
                            }else {

                                adapter.update(mCases);
                                recyclerView.loadMoreComplete();
                                ToastUtil.showShortToast("加载成功");
                                //AuditActivity.adapter.notifyDataSetChanged();
                            }



                            break;
                        case "003":

                            checkInvalidDialog();
                            LoadingDailogUtil.cancelLoadingDailog();
                            break;
                        default:
                            LoadingDailogUtil.cancelLoadingDailog();
                            ToastUtil.showShortToast("服务器错误");



                    }
                    break;
                }catch (Exception e){
                    e.printStackTrace();
                    LoadingDailogUtil.cancelLoadingDailog();
                    ToastUtil.showShortToast("未知错误");

                }

        }
    }

    private void checkInvalidDialog(){
        final NormalDialog dialog = new NormalDialog(MyApplication.getContext());
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
                Intent intent = new Intent(MyApplication.getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }
}
