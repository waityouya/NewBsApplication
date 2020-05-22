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
import com.example.myapplication.ui.activitys.IllegalCaseDetailectivity;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private ArrayList<Case> list;
    public final int TYPE_EMPTY = 0;
    public final int TYPE_NORMAL = 1;


    public void update(ArrayList<Case> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public CaseAdapter(ArrayList<Case> list) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_case, parent, false);
            return new ViewHolder(view);
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
            ViewHolder1.iv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, IllegalCaseDetailectivity.class);
                    Bundle bundle =new Bundle();
                    bundle.putSerializable("case",case1);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
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

        ImageView iv_right;
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
            iv_right = view.findViewById(R.id.iv_more);


        }

    }

    /**
     * 逻辑5：在Adapter中设置一个过滤方法，目的是为了将过滤后的数据传入Adapter中并刷新数据
     *
     * @param locationListModels
     */
    public void setFilter(ArrayList<Case> locationListModels) {

        list = new ArrayList<>();

        list.addAll(locationListModels);

        notifyDataSetChanged();

    }

    /**
     * 逻辑6：
     * 设置一个关闭过滤的方法， 目的是在上拉加载更多的时候将真实数据源传递给Adapter并刷新数据
     *
     * @param allList
     */
    public void closeFilter(ArrayList<Case> allList) {

        list = allList;
        notifyDataSetChanged();
    }
}
