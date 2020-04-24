package com.example.myapplication.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FirstFunctionAdapter;
import com.example.myapplication.model.UserMenu;
import com.example.myapplication.ui.activitys.AuditActivity;
import com.example.myapplication.ui.activitys.FindActivity;
import com.example.myapplication.ui.activitys.UpActivity;
import com.example.myapplication.util.MyApplication;
import com.example.myapplication.util.OnRecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirsrtFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FirsrtFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirsrtFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private ArrayList<UserMenu> list;
    private FirstFunctionAdapter adapter;

    public FirsrtFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirsrtFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirsrtFragment newInstance(String param1, String param2) {
        FirsrtFragment fragment = new FirsrtFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_firsrt, container, false);
        initView(view);
        setmListener();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void initView(View view){
        recyclerView = view.findViewById(R.id.rl_function);
        list = new ArrayList<>();
        UserMenu userMenu1 = new UserMenu(R.mipmap.up,"录入");
        UserMenu userMenu2 = new UserMenu(R.mipmap.find,"查找");
        UserMenu userMenu3 = new UserMenu(R.mipmap.shenhe,"审核");
        list.add(userMenu1);
        list.add(userMenu2);
        list.add(userMenu3);
         adapter = new FirstFunctionAdapter(list);
        GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(),3);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

    }

    private void setmListener(){
        adapter.setItemClickListener(new OnRecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                switch (position){
                    case 0:
                        Intent intent = new Intent(MyApplication.getContext(), UpActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                         intent = new Intent(MyApplication.getContext(), FindActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MyApplication.getContext(), AuditActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
