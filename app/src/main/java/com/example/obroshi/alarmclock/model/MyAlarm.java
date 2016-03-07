package com.example.obroshi.alarmclock.model;


import android.support.v7.widget.SwitchCompat;
import com.orm.SugarRecord;

import org.joda.time.DateTime;

public class MyAlarm extends SugarRecord{

    private Long mId;
    private String mFormattedAlarmTime;
    private String mAlarmLabel;
    private long mRawAlarmTime;
    private SwitchCompat mAlarmSwitch;
    private String mEventId;

    public MyAlarm(){
    }

    public MyAlarm(String eventsId, long rawAlarmTime){
        this.mEventId = eventsId;
        this.mRawAlarmTime = rawAlarmTime;
//        mAlarmSwitch.setChecked(true);
    }

    public MyAlarm(String eventsId, long rawAlarmTime, String label){
        this.mEventId = eventsId;
        this.mRawAlarmTime = rawAlarmTime;
        this.mAlarmLabel = label;
//        mAlarmSwitch.setChecked(true);
    }

    public String getFormattedAlarmTime() {
        DateTime dateTime = new DateTime(getRawAlarmTime());
        return String.format("%02d:%02d", dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }


    public String getAlarmLabel() {
        return mAlarmLabel;
    }

    public long getRawAlarmTime() {
        return mRawAlarmTime;
    }

    public void setAlarmLabel(String alarmLabel) {
        this.mAlarmLabel = mAlarmLabel;
    }

    public SwitchCompat getAlarmState() {
        return mAlarmSwitch;
    }

    public void setAlarmState(SwitchCompat state) {
        this.mAlarmSwitch = state;
    }

    public String getEventId() {
        return mEventId;
    }

    public Long getId() {
        return mId;
    }
}
