package com.example.myapplication.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MsgContentFragmentAdapte;
import com.google.android.material.tabs.TabLayout;

public class AuditActivity extends AppCompatActivity {
    private TextView title;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static MsgContentFragmentAdapte adapter;
    public static AuditActivity auditActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(auditActivity == null){
            auditActivity = this;
        }
        setContentView(R.layout.activity_audit);
        initView();
    }

    public static  AuditActivity getAudiActivity(){
        return auditActivity;
    }
    private void initView(){
        title = findViewById(R.id.tv_title);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        adapter = new MsgContentFragmentAdapte(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        title.setText("审核");
    }
}
