package com.example.obroshi.alarmclock.model;


import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

public class MyAlarm {

    private String mAlarmTime;
    private String mAlarmLabel;
    private SwitchCompat mAlarmSwitch;


    public MyAlarm(String alarmTime){
        this.mAlarmTime = alarmTime;
//        mAlarmSwitch.setChecked(true);
    }

    public MyAlarm(String alarmTime, String label){
        this.mAlarmTime = alarmTime;
        this.mAlarmLabel = label;
//        mAlarmSwitch.setChecked(true);
    }

    public String getAlarmTime() {
        return mAlarmTime;
    }

    public void setAlarmTime(String mAlarmTime) {
        this.mAlarmTime = mAlarmTime;
    }

    public String getAlarmLabel() {
        return mAlarmLabel;
    }

    public void setAlarmLabel(String mAlarmLabel) {
        this.mAlarmLabel = mAlarmLabel;
    }

    public SwitchCompat getAlarmState() {
        return mAlarmSwitch;
    }

    public void setAlarmState(SwitchCompat state) {
        this.mAlarmSwitch = state;
    }
}
