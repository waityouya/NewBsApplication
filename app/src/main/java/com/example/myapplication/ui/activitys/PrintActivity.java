package com.example.myapplication.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CaseAdapter;
import com.example.myapplication.adapter.PrintCaseAapter;
import com.example.myapplication.model.Case;
import com.example.myapplication.model.Page;
import com.example.myapplication.util.Contract;
import com.example.myapplication.util.GlobalHandler;
import com.example.myapplication.util.JsonUtils;
import com.example.myapplication.util.LoadingDailogUtil;
import com.example.myapplication.util.OkHttpUtils;
import com.example.myapplication.util.RSAUtils;
import com.example.myapplication.util.ToastUtil;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PrintActivity extends AppCompatActivity implements GlobalHandler.HandleMsgListener {
    private TextView textViewTitle;
    private RecyclerView mRecyclerView;
    private ArrayList<Case> mCases = new ArrayList<>();
    private SharedPreferences sp;

    //private View emptyView;
    private GlobalHandler mHandler;
    PrintCaseAapter adapter;


    private ImageView back;
    String token;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        initView();
        getData( userId, token);
        setLister();
    }

    private void initView() {
        back = findViewById(R.id.iv_title);

        textViewTitle = findViewById(R.id.tv_title);
        textViewTitle.setText("打印罚单");
        mRecyclerView = findViewById(R.id.print_case_recyclerView);
        sp = getSharedPreferences("login", 0);
        token = sp.getString("token", null);
        userId = sp.getString("userId", null);
        mHandler = GlobalHandler.getInstance();
        mHandler.setHandleMsgListener(this);
        // emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view,null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        //mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        mRecyclerView.setLayoutManager(layoutManager);
        //  mRecyclerView.setEmptyView(emptyView);

        adapter = new PrintCaseAapter(mCases);
        mRecyclerView.setAdapter(adapter);


    }

    private void setLister() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void getData( String userId, String token) {
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        Page page = new Page(userId, token);
        String getDataJson = JsonUtils.conversionJsonString(page);
        try {
            String publicEncryptJson = RSAUtils.publicEncrypt(getDataJson, RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
            okHttpUtils.postInfo(Contract.SERVER_ADDRESS + "PoliceGetMyUpCase", publicEncryptJson);
        } catch (Exception e) {
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
        switch (msg.what) {
            case 0:
                ToastUtil.showShortToast("网络出错，请检查网络");
                LoadingDailogUtil.cancelLoadingDailog();
                break;
            case 1:
                //也可以用这个接收
                try {
                    JSONObject jsonObject = new JSONObject(msg.getData().getString("backInfo"));
                    switch (jsonObject.getString("code")) {
                        case "ok":

                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            for (int i = 0; i < jsonArray.length(); i++) {
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
                            adapter.update(mCases);

                            LoadingDailogUtil.cancelLoadingDailog();

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
                } catch (Exception e) {
                    e.printStackTrace();
                    LoadingDailogUtil.cancelLoadingDailog();
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

    private void checkInvalidDialog() {
        final NormalDialog dialog = new NormalDialog(PrintActivity.this);
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
                Intent intent = new Intent(PrintActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
