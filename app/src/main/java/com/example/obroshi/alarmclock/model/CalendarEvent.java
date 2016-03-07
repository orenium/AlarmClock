package com.example.obroshi.alarmclock.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarEvent implements Parcelable {
    private final String TAG = CalendarEvent.class.getSimpleName();
    private final String mCalendarID;
    private final String mEventID;
    private final String mTitle;
    private final long mRawStartingTime;
    private final String mEndingTime;
    private String mLocation;
    private final String mDescription;
    private final String mCalendarName;
    private final int mCalendarColor;
    private final DateTime mDate;
    private long mRawAlarmTime;
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

    private CalendarEvent(Parcel in) {
        this.mCalendarID = in.readString();
        this.mEventID = in.readString();
        this.mTitle = in.readString();
        this.mEndingTime = in.readString();
        this.mLocation = in.readString();
        this.mDescription = in.readString();
        this.mCalendarName = in.readString();
        this.mRawAlarmTime = in.readLong();
        this.mHasAlarm = (1 == in.readInt());
        this.mCalendarColor = in.readInt();
        this.mRawStartingTime = in.readLong();
        this.mDate = new DateTime(in.readLong());
    }

    public int getCalendarColor() {
        return mCalendarColor;
    }

    public int getMonthOfYear() {
        return this.mDate.getMonthOfYear();
    }

    public int getDayInMonth() {
        return mDate.getDayOfMonth();
    }

    public boolean isHasAlarm() {
        return mHasAlarm;
    }

    public void setHasAlarm(boolean mHasAlarm) {
        this.mHasAlarm = mHasAlarm;
    }

    public long getAlarmTime() {
        return mRawAlarmTime;
    }

    public void setAlarmTime(long mAlarmTime) {
        this.mRawAlarmTime = mAlarmTime;
        this.mHasAlarm = true;
    }

    public String getCalendarName() {
        return mCalendarName;
    }

    public String getCalendar_ID() {
        return mCalendarID;
    }

    public String getEvent_ID() {
        return mEventID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getStartingTime() {
        return String.format("%02d:%02d", mDate.getHourOfDay(), mDate.getMinuteOfHour());
    }

    public long getRawStartingTime() {
        return mRawStartingTime;
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

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCalendarID);
        dest.writeString(mEventID);
        dest.writeString(mTitle);
        dest.writeString(mEndingTime);
        dest.writeString(mLocation);
        dest.writeString(mDescription);
        dest.writeString(mCalendarName);
        dest.writeLong(mRawAlarmTime);
        dest.writeInt(mHasAlarm ? 1 : 0);
        dest.writeInt(mCalendarColor);
        dest.writeLong(mRawStartingTime);
        dest.writeLong(this.mDate.getMillis());
    }

    public static final Parcelable.Creator<CalendarEvent> CREATOR
            = new Parcelable.Creator<CalendarEvent>() {
        public CalendarEvent createFromParcel(Parcel in) {
            return new CalendarEvent(in);
        }

        public CalendarEvent[] newArray(int size) {
            return new CalendarEvent[size];
        }
    };
}




