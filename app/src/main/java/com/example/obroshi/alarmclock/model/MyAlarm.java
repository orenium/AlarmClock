package com.example.obroshi.alarmclock.model;


import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import com.orm.SugarRecord;

public class MyAlarm extends SugarRecord{

    private Long mId;
    private String mAlarmTime;
    private String mAlarmLabel;
    private SwitchCompat mAlarmSwitch;
    private String mEventId;


    public MyAlarm(String eventsId, String alarmTime){
        this.mEventId = eventsId;
        this.mAlarmTime = alarmTime;
//        mAlarmSwitch.setChecked(true);
    }

    public MyAlarm(String eventsId, String alarmTime, String label){
        this.mEventId = eventsId;
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

    public Long getId() {
        return mId;
    }
}
