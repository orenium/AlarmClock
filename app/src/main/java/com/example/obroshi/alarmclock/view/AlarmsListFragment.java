package com.example.obroshi.alarmclock.view;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.obroshi.alarmclock.R;
import com.example.obroshi.alarmclock.controller.Controller;
import com.example.obroshi.alarmclock.model.MyAlarm;

import java.util.ArrayList;
import java.util.List;

public class AlarmsListFragment extends Fragment {

    private final String TAG = AlarmsListFragment.class.getSimpleName();

    public static final String ALARM_TIME = "alarmTime";
    public static final String LABEL = "label";

    private RecyclerView mRecyclerView;
    private AlarmsAdapter mAdapter;
    private List<MyAlarm> myAlarmList = new ArrayList<>();

    private Controller.onAlarmAdded mAddAlarmListener;
    private TextView mEmptyMsg;


    public AlarmsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        mAddAlarmListener = (Controller.onAlarmAdded) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarms_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.alarmsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.hasFixedSize();
        mAdapter = new AlarmsAdapter(getContext(), myAlarmList);
        mRecyclerView.setAdapter(mAdapter);
        mEmptyMsg = (TextView) view.findViewById(R.id.noAlarmsMsg);


    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            String time = args.getString(ALARM_TIME);
            String label = args.getString(LABEL);
            if (!label.isEmpty()){
                myAlarmList.add(new MyAlarm(time, label));
            } else {
                myAlarmList.add(new MyAlarm(time));
            }
        }
        if (myAlarmList.size() > 0){
            mEmptyMsg.setVisibility(View.GONE);
        } else {
            mEmptyMsg.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }


}
