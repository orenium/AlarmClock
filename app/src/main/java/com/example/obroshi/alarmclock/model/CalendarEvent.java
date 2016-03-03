package com.example.obroshi.alarmclock.model;

import org.joda.time.DateTime;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Lital Noa on 28/01/2016.
 */
public class CalendarEvent {
    private final String TAG = CalendarEvent.class.getSimpleName();
    private String mCalendarID;
    private String mEventID;
    private String mTitle;
    private long mRawStartingTime;
    private String mEndingTime;
    private String mLocation;
    private Boolean mAllDayEvent;
    private String mDescription;
    private String mCalendarName;
    private int mCalendarColor;
    private String mDayInMonth;
    private DateTime mDate;
    private String mAlarmTime;
    private boolean mHasAlarm;

    public CalendarEvent(String calendar_ID, String event_ID, String title, long rawStartingTime, String endingTime, String location, String description, String calendarName, int calendarColor) {
        this.mCalendarID = calendar_ID;
        this.mEventID = event_ID;
        this.mTitle = title;
        this.mRawStartingTime = rawStartingTime;
        this.mEndingTime = endingTime;
        this.mLocation = location;
        this.mDescription = description;
        this.mCalendarName = calendarName;
        this.mCalendarColor = calendarColor;
        this.mDate = new DateTime(rawStartingTime);
        this.setHasAlarm(false);
    }

    public int getCalendarColor() {
        return mCalendarColor;
    }

    public void setCalendarColor(int calendarColor) {
        this.mCalendarColor = calendarColor;
    }

    public String getMonthShortName() {
        switch (mDate.getMonthOfYear()) {
            case 1:
                return "jan";
            case 2:
                return "feb";
            case 3:
                return "mar";
            case 4:
                return "apr";
            case 5:
                return "may";
            case 6:
                return "jun";
            case 7:
                return "jul";
            case 8:
                return "aug";
            case 9:
                return "sep";
            case 10:
                return "oct";
            case 11:
                return "nov";
            case 12:
                return "dec";
            default:
                break;
        }
        return null;
    }

    public String getDayInMonth() {
        int day = mDate.getDayOfMonth();
        return day < 10 ? "0"+day: java.lang.String.valueOf(day);
    }

    public boolean isHasAlarm() {
        return mHasAlarm;
    }

    public void setHasAlarm(boolean mHasAlarm) {
        this.mHasAlarm = mHasAlarm;
    }

    public String getAlarmTime() {
        return mAlarmTime;
    }

    public void setAlarmTime(String mAlarmTime) {
        this.mAlarmTime = mAlarmTime;
        this.mHasAlarm = true;
    }

    public String getCalendarName() {
        return mCalendarName;
    }

    public void setCalendarName(String mCalendarName) {
        this.mCalendarName = mCalendarName;
    }

    public String getCalendar_ID() {
        return mCalendarID;
    }

    public void setCalendar_ID(String mCalendar_ID) {
        this.mCalendarID = mCalendar_ID;
    }

    public String getEvent_ID() {
        return mEventID;
    }

    public void setEvent_ID(String mEvent_ID) {
        this.mEventID = mEvent_ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mSubject) {
        this.mTitle = mSubject;
    }

    public String getFormattedStartingTime() {
        int hh = mDate.getHourOfDay();
        int mm = mDate.getMinuteOfHour();
        String hour = hh > 9 ?java.lang.String.valueOf(hh) : "0"+ java.lang.String.valueOf(hh);
        String min = mm > 9 ?java.lang.String.valueOf(mm) : "0"+ java.lang.String.valueOf(mm);
        return hour + ":" + min;
    }

    public long getRawStartingTime() {
        return mRawStartingTime;
    }

    public void setStartingTime(long startingTime) {
        this.mRawStartingTime = startingTime;

    }

    public String getEndingTime() {
        if (mEndingTime != null) {
            long unixSeconds = Long.parseLong(mEndingTime);
            Date date = new Date(unixSeconds);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);
        } else return "";
    }

    public void setEndingTime(String mEndingTime) {
        this.mEndingTime = mEndingTime;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public Boolean getAllDayEvent() {
        return mAllDayEvent;
    }

    public void setAllDayEvent(Boolean mAllDayEvent) {
        this.mAllDayEvent = mAllDayEvent;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String desc) {
        this.mDescription = desc;
    }
}




