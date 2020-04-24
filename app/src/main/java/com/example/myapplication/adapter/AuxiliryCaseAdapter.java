package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.AuxiliryCase;
import com.example.myapplication.ui.activitys.AuditDetailActivity;
import com.example.myapplication.util.LogUtil;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class AuxiliryCaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;

    private ArrayList<AuxiliryCase> list;
    public final int TYPE_EMPTY = 0;
    public final int TYPE_NORMAL = 1;



    public void update(ArrayList<AuxiliryCase> list) {
        LogUtil.d("update");
        this.list = list;
        notifyDataSetChanged();
    }

    public AuxiliryCaseAdapter(ArrayList<AuxiliryCase> list) {
        this.list = list;
    }

    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view;
        if(viewType == TYPE_EMPTY){
            view = LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false);
            return new RecyclerView.ViewHolder(view){};
        }else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_auxiliry_case, parent, false);
            return new AuxiliryCaseAdapter.ViewHolder(view);
        }

    }
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof AuxiliryCaseAdapter.ViewHolder){
            AuxiliryCaseAdapter.ViewHolder ViewHolder1 = (AuxiliryCaseAdapter.ViewHolder) viewHolder;
            final AuxiliryCase case1 = list.get(position);
            ViewHolder1.tv_name.setText(case1.getOffName());
            ViewHolder1.tv_id.setText( case1.getOffCertificateNumber());
            ViewHolder1.tv_off_type.setText( case1.getOffType());
            ViewHolder1.tv_off_time.setText( case1.getOffTime());
            switch (case1.getAuditType()){
                case 0:
                    ViewHolder1.tv_off_aduit_type.setText("未审核");
                    break;
                case 1:
                    ViewHolder1.tv_off_aduit_type.setText("已通过");
                    break;
                case 2:
                    ViewHolder1.tv_off_aduit_type.setText("未通过");
                    break;
            }


            ViewHolder1.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AuditDetailActivity.class);

                    intent.putExtra("auxiliaryId",case1.getAuxiliaryCaseId());

                    mContext.startActivity(intent);
                }
            });


        }


    }

    @Override
    public int getItemViewType(int position) {
        if(list.size()<=0){
            return TYPE_EMPTY;
        }
        return  TYPE_NORMAL;
    }


    public int getItemCount() {
        if (list.size()<=0){
            return 1;
        }
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_id;
        TextView tv_name;
        TextView tv_off_type;
        TextView tv_off_time;
        TextView tv_off_aduit_type;
        ImageView imageView;


        ViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_off_name_item_auxiliary);
            tv_id = view.findViewById(R.id.tv_off_id_item_auxiliary);
            tv_off_type = view.findViewById(R.id.tv_off_type_item_auxiliary);
            tv_off_time = view.findViewById(R.id.tv_off_time_item_auxiliary);
            tv_off_aduit_type= view.findViewById(R.id.tv_audit_type_item_auxiliary);
            imageView = view.findViewById(R.id.iv_more);


        }

    }
}
