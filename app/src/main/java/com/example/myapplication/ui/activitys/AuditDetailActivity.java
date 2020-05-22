package com.example.myapplication.ui.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AuxiliaryDetailAdapter;
import com.example.myapplication.adapter.CaseAdapter;
import com.example.myapplication.model.AuxiliaryParams;
import com.example.myapplication.model.AuxiliryCase;
import com.example.myapplication.model.AuxiliryCaseDetail;
import com.example.myapplication.model.Case;
import com.example.myapplication.model.JsonRootBean;
import com.example.myapplication.model.Page;
import com.example.myapplication.model.ReturnAuxiliryCase;
import com.example.myapplication.model.ReturnLoginInfo;
import com.example.myapplication.util.Base64Utils;
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
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.goyourfly.multi_picture.ImageLoader;
import com.goyourfly.multi_picture.MultiPictureView;
import com.goyourfly.vincent.Vincent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AuditDetailActivity extends AppCompatActivity implements GlobalHandler.HandleMsgListener{
    private RecyclerView recyclerView;
    private MultiPictureView multiPictureView;
    private AuxiliaryDetailAdapter adapter;
    private ArrayList<AuxiliryCaseDetail> mCases = new ArrayList<>();
    private EditText editTextAudit;
    private Button buttonAudit;
    private RadioGroup radioGroup ;
    private ImageView back;
    RadioButton radioButton ;
    String token;
    String userId;
    private PhotoView photoView;
    private Case case1;
    private AuxiliryCase auxiliryCase;
    private View parent;
    private int auxiliaryId = 0;
    private SharedPreferences sp ;
    //private View emptyView;
    private GlobalHandler mHandler;
    private Boolean isAudit = false;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_detail);
        init();
        getData(auxiliaryId,userId,token);
        setmLinster();
    }

    private void init(){
        back = findViewById(R.id.iv_title);
        recyclerView = findViewById(R.id.audit_detail_recyclerView);
        multiPictureView = findViewById(R.id.multi_image_view_detail);
        radioGroup = findViewById(R.id.radioGroup);
        buttonAudit = findViewById(R.id.audit_add);
        editTextAudit = findViewById(R.id.et_audit);
        photoView = findViewById(R.id.photoview);
        textView = findViewById(R.id.tv_title);
        auxiliaryId   = getIntent().getIntExtra("auxiliaryId",0);
        LogUtil.d("id"+auxiliaryId);
        MultiPictureView.setImageLoader(new ImageLoader() {
            @Override
            public void loadImage(@NonNull ImageView imageView, @NonNull Uri uri) {
                Vincent.with(AuditDetailActivity.this)
                        .load(uri)
                        .placeholder(R.drawable.ic_placeholder_loading)
                        .error(R.drawable.ic_placeholder_loading)
                        .into(imageView);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AuxiliaryDetailAdapter(mCases);
        recyclerView.setAdapter(adapter);
        parent = findViewById(R.id.sl);
        sp = getSharedPreferences("login", 0);
        token = sp.getString("token",null);
        userId = sp.getString("userId",null);
        mHandler = GlobalHandler.getInstance();
        mHandler.setHandleMsgListener(this);
        textView.setText("审核");
    }

    private void setmLinster(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String audit = editTextAudit.getText().toString();
                if(audit.isEmpty()){
                    ToastUtil.showShortToast("请完善数据");
                }else {
                    LoadingDailogUtil.showLoadingDialog(AuditDetailActivity.this,"提交中..");
                    radioButton =findViewById(radioGroup.getCheckedRadioButtonId());
                    int aduitType = 1;
                    if(radioButton.getText().toString().equals("未通过")){
                        aduitType = 2;
                    }
                    if( auxiliryCase != null){
                        auxiliryCase.setAuditType(aduitType);
                        auxiliryCase.setAuditorId(Integer.valueOf(userId));
                        auxiliryCase.setAudit(audit);
                        auxiliryCase.setAppToken(token);
                        OkHttpUtils okHttpUtils = new OkHttpUtils();
                        String getDataJson = JsonUtils.conversionJsonString(auxiliryCase);
                        try {
                            isAudit = true;
                            String publicEncryptJson = RSAUtils.publicEncrypt(getDataJson,RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
                            okHttpUtils.postInfo(Contract.SERVER_ADDRESS+"PoliceAuditServlet",publicEncryptJson);
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

            }
        });

        multiPictureView.setItemClickCallback(new MultiPictureView.ItemClickCallback() {
            @Override
            public void onItemClicked(@NotNull View view, int i, @NotNull ArrayList<Uri> arrayList) {
                photoView.setImageURI(arrayList.get(i));
                photoView.setVisibility(View.VISIBLE);
                parent.setVisibility(View.GONE);

            }
        });

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                photoView.setVisibility(View.GONE);
                parent.setVisibility(View.VISIBLE);

            }
        });
    }

    private void getData(int auxiliaryId,String userId,String token){
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        AuxiliaryParams auxiliaryParams = new AuxiliaryParams(auxiliaryId,userId,token);
        String getDataJson = JsonUtils.conversionJsonString(auxiliaryParams);
        try {
            String publicEncryptJson = RSAUtils.publicEncrypt(getDataJson,RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
            okHttpUtils.postInfo(Contract.SERVER_ADDRESS+"GetAuxiliaryOneCaseServlet",publicEncryptJson);
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
    public void onBackPressed() {
        LogUtil.d("返回");
        if (photoView.getVisibility() == View.VISIBLE) {

            photoView.setVisibility(View.GONE);
            parent.setVisibility(View.VISIBLE);

        } else {
            super.onBackPressed();
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
                if(!isAudit) {
                    try {
                        ReturnAuxiliryCase returnAuxiliryCase = new Gson().fromJson(msg.getData().getString("backInfo"), ReturnAuxiliryCase.class);
                        auxiliryCase = returnAuxiliryCase.getData();
                        switch (returnAuxiliryCase.getCode()) {
                            case "ok":
                                LogUtil.d("ok");
                                auxiliryCase = returnAuxiliryCase.getData();
                                for (int i = 0; i < 12; i++) {
                                    switch (i) {
                                        case 0:
                                            AuxiliryCaseDetail caseDetail = new AuxiliryCaseDetail(R.drawable.xingming, "姓名:", auxiliryCase.getOffName());
                                            mCases.add(caseDetail);
                                            break;
                                        case 1:
                                            AuxiliryCaseDetail caseDetail1 = new AuxiliryCaseDetail(R.drawable.jiguan, "籍贯:", auxiliryCase.getOffBirthPlace());
                                            mCases.add(caseDetail1);
                                            break;
                                        case 2:
                                            AuxiliryCaseDetail caseDetail2 = new AuxiliryCaseDetail(R.drawable.zhengjianleix, "证件类型:", auxiliryCase.getOffCertificateType());
                                            mCases.add(caseDetail2);
                                            break;
                                        case 3:
                                            AuxiliryCaseDetail caseDetail3 = new AuxiliryCaseDetail(R.drawable.zhengjianhaoma, "证件号码:", auxiliryCase.getOffCertificateNumber());
                                            mCases.add(caseDetail3);
                                            AuxiliryCaseDetail caseDetail12 = new AuxiliryCaseDetail(R.drawable.chepai, "车辆号码:", auxiliryCase.getOffPlateNumber());
                                            mCases.add(caseDetail12);
                                            break;
                                        case 4:
                                             caseDetail3 = new AuxiliryCaseDetail(R.drawable.zhengjianhaoma, "证件号码:", auxiliryCase.getOffCertificateNumber());
                                            mCases.add(caseDetail3);
                                            break;

                                        case 5:
                                            AuxiliryCaseDetail caseDetail4 = new AuxiliryCaseDetail(R.drawable.weifashij, "违法时间:", auxiliryCase.getOffTime());
                                            mCases.add(caseDetail4);
                                            break;

                                        case 6:
                                            AuxiliryCaseDetail caseDetail5 = new AuxiliryCaseDetail(R.drawable.weifadidian, "违法地点:", auxiliryCase.getOffPlace());
                                            mCases.add(caseDetail5);
                                            break;
                                        case 7:
                                            AuxiliryCaseDetail caseDetail6 = new AuxiliryCaseDetail(R.drawable.zhonglei, "违法类型:", auxiliryCase.getOffType());
                                            mCases.add(caseDetail6);
                                            break;

                                        case 8:
                                            AuxiliryCaseDetail caseDetail7 = new AuxiliryCaseDetail(R.drawable.chufa, "处罚方式:", auxiliryCase.getOffPunishmentType());
                                            mCases.add(caseDetail7);
                                            break;

                                        case 9:
                                            AuxiliryCaseDetail caseDetail8 = new AuxiliryCaseDetail(R.drawable.chufajine, "处罚金额:", String.valueOf(auxiliryCase.getOffMoney()));
                                            mCases.add(caseDetail8);
                                            break;

                                        case 10:
                                            AuxiliryCaseDetail caseDetail9 = new AuxiliryCaseDetail(R.drawable.jingyuan, "处罚人:", String.valueOf(auxiliryCase.getPunishmentId()));
                                            mCases.add(caseDetail9);
                                            break;

                                        case 11:
                                            AuxiliryCaseDetail caseDetail10 = new AuxiliryCaseDetail(R.drawable.jingyuan, "处罚人:", String.valueOf(auxiliryCase.getPunishmentName()));
                                            mCases.add(caseDetail10);
                                            break;


                                    }
                                }
                                adapter.update(mCases);
                                String[] images = auxiliryCase.getImages().split("[;]");
                                List<Uri> uriList = new LinkedList<>();
                                for (String s : images
                                ) {
                                    byte[] bitmapByte = Base64.decode(s, Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
                                    Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                                    uriList.add(uri);
                                }

                                multiPictureView.setList(uriList);

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
                }else {
                    JsonRootBean jsonRootBean = new Gson().fromJson(msg.getData().getString("backInfo"), JsonRootBean.class);
                    switch (jsonRootBean.getCode()){
                        case "ok":
                            LoadingDailogUtil.cancelLoadingDailog();
                            ok();
                            break;
                        case "003":
                            checkInvalidDialog();
                            LoadingDailogUtil.cancelLoadingDailog();
                            break;
                            default:
                                LoadingDailogUtil.cancelLoadingDailog();
                                ToastUtil.showShortToast("服务器错误");
                    }
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
        final NormalDialog dialog = new NormalDialog(AuditDetailActivity.this);
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
                Intent intent = new Intent(AuditDetailActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void ok(){
        BounceTopEnter mBasIn = new BounceTopEnter();
        // 退出动画
        SlideBottomExit mBasOut = new SlideBottomExit();
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("审核成功，是否打印罚单?") // （
                .showAnim(mBasIn) //
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    //取消
                    @Override
                    public void onBtnClick() {
                        finish();
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    //确定
                    @Override
                    public void onBtnClick() {

                        dialog.dismiss();
                        Intent intent = new Intent(AuditDetailActivity.this,PrinterSettingActivity.class);
                        case1 = new Case();
                        case1.setOffMoney(auxiliryCase.getOffMoney());
                        case1.setOffPunishmentType(auxiliryCase.getOffPunishmentType());
                        case1.setOffName(auxiliryCase.getOffName());
                        case1.setOffPlateNumber(auxiliryCase.getOffPlateNumber());
                        case1.setOffCertificateNumber(auxiliryCase.getOffCertificateNumber());
                        case1.setOffTime(auxiliryCase.getOffTime());
                        case1.setOffPlace(auxiliryCase.getOffPlace());
                        case1.setOffType(auxiliryCase.getOffType());
                        case1.setPunishmentName(auxiliryCase.getPunishmentName());
                        intent.putExtra("caseInfo",case1);


                        startActivity(intent);
                        finish();
                    }
                });



    }
}
