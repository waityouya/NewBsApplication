package com.example.myapplication.ui.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;


import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.adapter.UserFunctionAdapter;
import com.example.myapplication.model.ReturnLoginInfo;
import com.example.myapplication.model.ReturnUserInfo;
import com.example.myapplication.model.UserInfo;
import com.example.myapplication.model.UserMenu;
import com.example.myapplication.model.UserToken;
import com.example.myapplication.ui.activitys.MainActivity;
import com.example.myapplication.ui.activitys.MyUpCaseActivity;
import com.example.myapplication.ui.activitys.PrintActivity;
import com.example.myapplication.ui.activitys.UpdatePassWordActivity;
import com.example.myapplication.ui.activitys.UserActivity;
import com.example.myapplication.util.Contract;
import com.example.myapplication.util.GlobalHandler;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment implements GlobalHandler.HandleMsgListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "name";
    private static final String ARG_PARAM2 = "phoneNumber";

    private ImageView imageViewHead;


    // TODO: Rename and change types of parameters
    private String name;
    private String phoneNumber;
    private UserInfo mUserInfo;
    private GlobalHandler mHandler;
    private OnFragmentInteractionListener mListener;
    private SharedPreferences sp;
    String token;
    String userId;
    private TextView textViewUserName;
    private TextView textViewPhoneNumber;
    private List<UserMenu> list1;
    private List<UserMenu> list2;
    private ListView userFunctionListView1;
    private ListView userFunctionListView2;
    private UserFunctionAdapter adapter1;
    private UserFunctionAdapter adapter2;
    public MyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            phoneNumber = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        // Inflate the layout for this fragment
        mHandler = GlobalHandler.getInstance();
        mHandler.setHandleMsgListener(this);
        sp = MyApplication.getContext().getSharedPreferences("login", 0);
        token = sp.getString("token", null);
        userId = sp.getString("userId", null);
        initView(view);
        getData(userId, token);
        return view;
    }

    private void initView(View view){
        imageViewHead = view.findViewById(R.id.h_head);
        textViewUserName = view.findViewById(R.id.user_name);
        textViewPhoneNumber = view.findViewById(R.id.user_val);

        Glide.with(this).load(R.drawable.login1)
                .apply(RequestOptions.bitmapTransform(new CropCircleTransformation()))
                .into(imageViewHead);

        userFunctionListView1 = view.findViewById(R.id.user_function_list_view1);
        userFunctionListView2 = view.findViewById(R.id.user_function_list_view2);

        list1 = new ArrayList<>();
        UserMenu userMenu1 = new UserMenu(R.mipmap.chufa,"处罚记录");
        UserMenu userMenu2 = new UserMenu(R.mipmap.dayin,"打印");
        UserMenu userMenu3 = new UserMenu(R.mipmap.xiugaimima,"修改密码");
        UserMenu userMenu4 = new UserMenu(R.mipmap.shezhi,"设置");
        list1.add(userMenu1);
        list1.add(userMenu2);




        adapter1 = new UserFunctionAdapter(list1, MyApplication.getContext());
        userFunctionListView1.setDivider(new ColorDrawable(Color.parseColor("#E0EEE0")));
        userFunctionListView1.setDividerHeight(3);
        userFunctionListView1.setAdapter(adapter1);

        list2 = new ArrayList<>();
        list2.add(userMenu3);
        list2.add(userMenu4);

        adapter2 = new UserFunctionAdapter(list2, MyApplication.getContext());
        userFunctionListView2.setDivider(new ColorDrawable(Color.parseColor("#E0EEE0")));
        userFunctionListView2.setDividerHeight(3);
        userFunctionListView2.setAdapter(adapter2);

        userFunctionListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(getActivity(), MyUpCaseActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), PrintActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        userFunctionListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(getActivity(), UpdatePassWordActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        break;
                }
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void getData(String userId, String token) {
        OkHttpUtils okHttpUtils = new OkHttpUtils();
        UserToken userToken = new UserToken(userId,token);
        String getDataJson = JsonUtils.conversionJsonString(userToken);
        try {
            String publicEncryptJson = RSAUtils.publicEncrypt(getDataJson, RSAUtils.getPublicKey(RSAUtils.SERVER_PUBLIC_KEY));
            okHttpUtils.postInfo(Contract.SERVER_ADDRESS + "GetPoliceInfo", publicEncryptJson);
        } catch (Exception e) {
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
    public void onDestroy() {
        super.onDestroy();
        //销毁移除所有消息，避免内存泄露
        mHandler.removeCallbacks(null);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what){
            case 0:
                ToastUtil.showShortToast("网络出错，请检查网络");
                LoadingDailogUtil.cancelLoadingDailog();
                break;
            case 1:
                ReturnUserInfo returnUserInfo = new Gson().fromJson(msg.getData().getString("backInfo"),ReturnUserInfo.class);
                switch (returnUserInfo.getCode()){
                    case "ok":
                        mUserInfo = returnUserInfo.getData();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textViewUserName.setText(mUserInfo.getName());
                                textViewPhoneNumber.setText(mUserInfo.getPhoneNumber());

                            }

                        });
                        break;
                    case "003":
                        checkInvalidDialog();
                        break;
                    default:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.i("111");
                                ToastUtil.showShortToast("未知错误");
                                LoadingDailogUtil.cancelLoadingDailog();

                            }

                        });


                }
                break;
        }
    }
    private void checkInvalidDialog(){
        final NormalDialog dialog = new NormalDialog(getActivity());
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
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
