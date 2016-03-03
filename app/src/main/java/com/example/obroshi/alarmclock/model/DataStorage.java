package com.example.obroshi.alarmclock.model;

/**
 * Created by Lital Noa on 31/01/2016.
 */
public class DataStorage {

    private double mCurrentLat;
    private double mCurrentLng;
    private String mCurrentLocation;
    private String mDurationValue;
    private String mDurationInTrafficText;
    private String mDurationInTrafficValue;
    private String mRawStartingTime;

    private static DataStorage instance = null;

    public static DataStorage getInstance(){
        if (instance == null){
            instance = new DataStorage();
        }
        return instance;
    }

    public String getDurationValue() {
        return mDurationValue;
    }

    public void setDurationValue(String mDurationValue) {
        this.mDurationValue = mDurationValue;
    }

    public String getRawStartingTime() {
        return mRawStartingTime;
    }

    public void setRawStartingTime(String rawStartingTime) {
        mRawStartingTime = rawStartingTime;
    }

    public String getDurationInTrafficValue() {
        return mDurationInTrafficValue;
    }

    public void setDurationInTrafficValue(String mDurationInTrafficValue) {
        this.mDurationInTrafficValue = mDurationInTrafficValue;
    }

    public String getDurationInTrafficText() {
        return mDurationInTrafficText;
    }

    public void setDurationInTrafficText(String mDurationInTrafficText) {
        this.mDurationInTrafficText = mDurationInTrafficText;
    }

    public double getCurrentLat() {
        return mCurrentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.mCurrentLat = currentLat;
    }

    public double getCurrentLng() {
        return mCurrentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.mCurrentLng = currentLng;
    }

    public String getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.mCurrentLocation = currentLocation;
    }
}
