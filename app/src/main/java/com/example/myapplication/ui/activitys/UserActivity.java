package com.example.myapplication.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.example.myapplication.R;
import com.example.myapplication.model.ReturnLoginInfo;
import com.example.myapplication.model.UserToken;
import com.example.myapplication.ui.fragments.FirsrtFragment;
import com.example.myapplication.ui.fragments.MyFragment;
import com.example.myapplication.util.Contract;
import com.example.myapplication.util.DeepAssetUtil;
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
import com.google.gson.Gson;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements GlobalHandler.HandleMsgListener{
    private GlobalHandler mHandler;
    private SharedPreferences sp ;

    private FragmentManager fragmentManager;
//    private Fragment auditFaceFragment;
//    private Fragment findFragment;
//    private Fragment upFragment;
    private Fragment firstFragment;
    private Fragment myFragment;
    public long handle;
    private static  UserActivity userActivity;
    FragmentTransaction transaction;
    private String [] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,
    Manifest.permission.NFC,Manifest.permission.READ_PHONE_STATE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
    private List<String> mPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        sp = getSharedPreferences("login", 0);
        mHandler = GlobalHandler.getInstance();
        mHandler.setHandleMsgListener(this);
        userActivity = this;
        checkToken();
        initView();
        initPermission();

    }

    public static UserActivity getUserActivity(){
        return userActivity;
    }

    private void checkToken(){
        String token = sp.getString("token",null);
        String userId = sp.getString("userId",null);
        LogUtil.i(token+userId);
        if(token != null && userId != null){
            OkHttpUtils okHttpUtils = new OkHttpUtils();
            UserToken userToken = new UserToken(userId,token);
            String tokenJson = JsonUtils.conversionJsonString(userToken);
            try {
                String publicEncryptJson = RSAUtils.publicEncrypt(tokenJson,RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
                okHttpUtils.postInfo(Contract.SERVER_ADDRESS+"CheckTokenServlet",publicEncryptJson);

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


        }else {
            checkInvalidDialog();
        }
    }

    private void checkInvalidDialog(){
        final NormalDialog dialog = new NormalDialog(UserActivity.this);
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
                Intent intent = new Intent(UserActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void initView(){
        AHBottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("首页",R.drawable.frist);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("我的",R.drawable.user);


        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);

        bottomNavigation.setAccentColor(Color.parseColor("#6184FF"));
        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setTranslucentNavigationEnabled(true);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        //设第一个界面为默认界面
        if(firstFragment == null){
            firstFragment = FirsrtFragment.newInstance("1","2");
            transaction.add(R.id.content,firstFragment);
        }else {
            transaction.show(firstFragment);
        }
        transaction.commit();

        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                FragmentTransaction transaction= fragmentManager.beginTransaction();
                hideAllFragment(transaction);
                switch (position){
                    case 0:
                        if(firstFragment == null){
                            firstFragment = FirsrtFragment.newInstance("1","2");
                            transaction.add(R.id.content,firstFragment);
                        }else {
                            transaction.show(firstFragment);
                        }
                        break;
                    case 1:
                        if(myFragment == null){
                            myFragment = MyFragment.newInstance("1","2");
                            transaction.add(R.id.content,myFragment);
                        }else {
                            transaction.show(myFragment);
                        }
                        break;


                    default:
                        break;
                }
                transaction.commit();
                return true;
            }
        });
        //初始化OCR
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {

            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                LogUtil.d("onError", "msg: " + error.getMessage());
            }
        }, getApplicationContext(), "LlCcsGQj3CecWlnVKeSQtgqe", "GHGD62kNQpfqChhGZwbAeva53g2Kk4fT");
    }

    //隐藏Fragment界面
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(myFragment != null){
            fragmentTransaction.hide(myFragment);
        }
        if(firstFragment != null){
            fragmentTransaction.hide(firstFragment);
        }

    }

    //申请权限
    private void initPermission(){
        mPermissionList.clear();
        for (int i=0;i<permissions.length;i++){
            if(ContextCompat.checkSelfPermission(this,permissions[i])!= PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(permissions[i]);
            }
        }

        //申请权限
        if(mPermissionList.size() >0){
            ActivityCompat.requestPermissions(this,permissions,1);
        }
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
                        break;
                    default:
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("isTokenValid",false);
                        editor.commit();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkInvalidDialog();

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

        // 释放内存资源
        OCR.getInstance().release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            LogUtil.d( "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, getApplicationContext(), mLoaderCallback);
        } else {
            LogUtil.d("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @SuppressLint("StaticFieldLeak")
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            if (status == LoaderCallbackInterface.SUCCESS) {
                LogUtil.d( "OpenCV 加载成功");
                handle = DeepAssetUtil.initRecognizer(UserActivity.this);
            } else {
                LogUtil.d("OpenCV 加载失败");
            }
        }
    };
}
