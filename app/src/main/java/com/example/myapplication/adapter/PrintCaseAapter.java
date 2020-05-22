package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.Case;
import com.example.myapplication.ui.activitys.AuditDetailActivity;
import com.example.myapplication.ui.activitys.IllegalCaseDetailectivity;
import com.example.myapplication.ui.activitys.PrinterSettingActivity;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class PrintCaseAapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;

    private ArrayList<Case> list;
    public final int TYPE_EMPTY = 0;
    public final int TYPE_NORMAL = 1;


    public void update(ArrayList<Case> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public PrintCaseAapter(ArrayList<Case> list) {
        this.list = list;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view;
        if (viewType == TYPE_EMPTY) {
            view = LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_print_case, parent, false);
            return new PrintCaseAapter.ViewHolder(view);
        }

    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder ViewHolder1 = (ViewHolder) viewHolder;
            final Case case1 = list.get(position);
            ViewHolder1.tv_name.setText(case1.getOffName());
            ViewHolder1.tv_id.setText(case1.getOffCertificateNumber());
            ViewHolder1.tv_off_type.setText(case1.getOffType());
            ViewHolder1.tv_off_time.setText(case1.getOffTime());
            ViewHolder1.iv_print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isPrint(case1);
                }
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() <= 0) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }


    public int getItemCount() {
        if (list.size() <= 0) {
            return 1;
        }
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_print;
        TextView tv_id;
        TextView tv_name;
        TextView tv_off_type;
        TextView tv_off_time;


        ViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_off_name_item);
            tv_id = view.findViewById(R.id.tv_off_id_item);
            tv_off_type = view.findViewById(R.id.tv_off_type_item);
            tv_off_time = view.findViewById(R.id.tv_off_time_item);
            iv_print = view.findViewById(R.id.iv_print);


        }

    }

    private void isPrint(final Case case1){
        BounceTopEnter mBasIn = new BounceTopEnter();
        // 退出动画
        SlideBottomExit mBasOut = new SlideBottomExit();
        final NormalDialog dialog = new NormalDialog(mContext);
        dialog.content("是否打印\n身份证："+case1.getOffCertificateNumber()+"\n姓名:"+case1.getOffName()+"\n"+
                "违法时间:"+case1.getOffTime()+"\n的罚单?")
                .showAnim(mBasIn) //
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    //取消
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    //确定
                    @Override
                    public void onBtnClick() {

                        dialog.dismiss();
                        Intent intent = new Intent(mContext,PrinterSettingActivity.class);

                        intent.putExtra("caseInfo",case1);
                        mContext.startActivity(intent);
                    }
                });



    }


}
