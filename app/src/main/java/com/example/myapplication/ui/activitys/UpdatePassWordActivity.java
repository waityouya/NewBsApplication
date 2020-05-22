package com.example.myapplication.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.ReturnLoginInfo;
import com.example.myapplication.model.UpdatePassWord;
import com.example.myapplication.model.User;
import com.example.myapplication.util.Contract;
import com.example.myapplication.util.GlobalHandler;
import com.example.myapplication.util.JsonUtils;
import com.example.myapplication.util.LoadingDailogUtil;
import com.example.myapplication.util.MD5Util;
import com.example.myapplication.util.OkHttpUtils;
import com.example.myapplication.util.RSAUtils;
import com.example.myapplication.util.ToastUtil;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.google.gson.Gson;

public class UpdatePassWordActivity extends AppCompatActivity implements GlobalHandler.HandleMsgListener{

    private TextView textViewTitle;
    private EditText editTextUserBefore;
    private EditText editTextPasswordNew;
    private EditText getEditTextPaawordAgain;
    private Button buttonUpdate;
    private GlobalHandler mHandler;
    String token;
    String userId;
    private SharedPreferences sp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass_word);
        initWidget();
        setListen();
        sp = getSharedPreferences("login", 0);
        token = sp.getString("token",null);
        userId = sp.getString("userId",null);

    }
    private void initWidget(){
        mHandler = GlobalHandler.getInstance();
        textViewTitle = findViewById(R.id.tv_title);
        editTextUserBefore = findViewById(R.id.et_user_pass_word_before);
        editTextPasswordNew = findViewById(R.id.et_password);
        getEditTextPaawordAgain = findViewById(R.id.et_password_again);
        buttonUpdate = findViewById(R.id.user_update);
        textViewTitle.setText("修改密码");

    }

    private void setListen(){
        mHandler.setHandleMsgListener(this);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passWordBefore = editTextUserBefore.getText().toString();
                String passWordNew = editTextPasswordNew.getText().toString();
                String passWordAgain = getEditTextPaawordAgain.getText().toString();
                if(passWordBefore.isEmpty() || passWordNew.isEmpty() || passWordAgain.isEmpty()){
                    ToastUtil.showShortToast("请填写完整");
                }else if(!passWordNew.equals(passWordAgain)){
                    ToastUtil.showShortToast("两次密码不一致");
                }else {
                    LoadingDailogUtil.showLoadingDialog(UpdatePassWordActivity.this,"修改中...");
                    String passWordBeforeMd5 = MD5Util.MD5(passWordBefore);
                    String passWordNewMd5 = MD5Util.MD5(passWordNew);
                    OkHttpUtils okHttpUtils = new OkHttpUtils();
                    UpdatePassWord updatePassWord = new UpdatePassWord(passWordBeforeMd5,passWordNewMd5,userId,token);
                    String updateJson = JsonUtils.conversionJsonString(updatePassWord);

                    try {
                        String publicEncryptJson = RSAUtils.publicEncrypt(updateJson,RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
                        okHttpUtils.postInfo(Contract.SERVER_ADDRESS+"PoliceUpdatePassWord",publicEncryptJson);


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
            }
        });

    }

    private void successDialog(){
        final NormalDialog dialog = new NormalDialog(UpdatePassWordActivity.this);
        LoadingDailogUtil.cancelLoadingDailog();
        dialog.content("修改成功")
                .btnNum(1)
                .btnText("确定")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();
        dialog.setCancelable(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isTokenValid",false);
                editor.apply();
                Intent intent=new Intent(UpdatePassWordActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }
    private void failDialog(){
        final NormalDialog dialog = new NormalDialog(UpdatePassWordActivity.this);
        LoadingDailogUtil.cancelLoadingDailog();
        dialog.content("注册失败，原密码不正确")
                .btnNum(1)
                .btnText("确定")
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();
        dialog.setCancelable(false);
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();

            }
        });

    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what){
            case 0:
                ToastUtil.showShortToast("网络出错，请检查网络");
                break;
            case 1:
                ReturnLoginInfo returnLoginInfo = new Gson().fromJson(msg.getData().getString("backInfo"),ReturnLoginInfo.class);
                switch (returnLoginInfo.getCode()){
                    case "ok":
                        successDialog();
                        break;
                    case "003":
                        checkInvalidDialog();
                        break;
                    case "008":
                        failDialog();
                        break;
                    default:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShortToast("服务器出错");
                                LoadingDailogUtil.cancelLoadingDailog();

                            }

                        });


                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁移除所有消息，避免内存泄露
        mHandler.removeCallbacks(null);
    }
    private void checkInvalidDialog(){
        final NormalDialog dialog = new NormalDialog(UpdatePassWordActivity.this);
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
                Intent intent = new Intent(UpdatePassWordActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
