package com.example.myapplication.adapter;

import android.os.Bundle;

import com.example.myapplication.ui.fragments.MsgContentFragment;
import com.example.myapplication.ui.fragments.NotPassFragment;
import com.example.myapplication.ui.fragments.PassFragment;
import com.example.myapplication.util.LogUtil;

import java.util.LinkedList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class MsgContentFragmentAdapte extends FragmentPagerAdapter {
    private List<String> names = new LinkedList<>();

    public MsgContentFragmentAdapte(FragmentManager fm) {
        super(fm);

        names.add("未审核");
        names.add("已通过");
        names.add("未通过");
    }

    @Override
    public Fragment getItem(int position) {



        if(position == 0){
            LogUtil.d("0");
            MsgContentFragment fragment = new MsgContentFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("auditType",0);
            fragment.setArguments(bundle);
            return fragment;
        }else if(position == 1){
            LogUtil.d("1");
            PassFragment fragment = new PassFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("auditType",1);
            fragment.setArguments(bundle);
            return fragment;
        }else {
            LogUtil.d("2");
            NotPassFragment fragment = new NotPassFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("auditType",2);
            fragment.setArguments(bundle);
            return fragment;
        }

    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String plateName = names.get(position);
        if (plateName == null) {
            plateName = "";
        } else if (plateName.length() > 15) {
            plateName = plateName.substring(0, 15) + "...";
        }
        return plateName;
    }
//    @Override
//    public int getItemPosition(Object object) {
//        return PagerAdapter.POSITION_NONE;
//    }

}
