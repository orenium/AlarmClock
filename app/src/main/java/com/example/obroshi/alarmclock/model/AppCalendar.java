package com.example.obroshi.alarmclock.model;

public class AppCalendar {
    private final long mId;
    private final String mName;
    private final int mColor;
    private boolean mSelected;

    public AppCalendar(long id, String name, int color){
        this.mId = id;
        this.mName = name;
        this.mColor = color;
        this.mSelected = false;
    }

    public long getId() {
        return mId;
    }

    public String getDisplayName() {
        return mName;
    }

    public int getColor() {
        return mColor;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }

    public boolean isSelected() {
        return this.mSelected;
    }
}
