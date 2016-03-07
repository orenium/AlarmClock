package com.example.obroshi.alarmclock.controller;

import android.content.Context;

import com.example.obroshi.alarmclock.model.AppCalendar;
import com.example.obroshi.alarmclock.model.CalendarEvent;
import com.example.obroshi.alarmclock.model.DataStorage;
import com.example.obroshi.alarmclock.model.GetCalendarData;
import com.example.obroshi.alarmclock.model.GetDirectionsData;
import com.example.obroshi.alarmclock.model.GetDistanceMatrixData;
import com.example.obroshi.alarmclock.model.GetLocationData;
import com.example.obroshi.alarmclock.model.WakeUpCalculation;
import com.example.obroshi.alarmclock.view.AlarmDataFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private GetLocationData mGetLocationData;
    private GetCalendarData mGetCalendarData;
    private DataStorage mDataStorage;
    private WakeUpCalculation mWakeUpCalculation;

    private static Controller instance = null;

    public interface CalendarCallback {
        public void onDataReceived(List<CalendarEvent> events);
        public void onCalendarsListReceived(ArrayList<AppCalendar> calendars);
    }

    public interface LocationCallback {
        public void onCurrentLocationReceived(double lat, double lng);
    }

    public interface WakeUpDataCallback {
        public void onWakeUpTimeReceived(long RawWakeupTime, String formattedWakeUpTime, String hours, String minutes);
        public void onFailed(int error);
    }

    public interface onEventSelectedListener {
        public void onEventSelected(CalendarEvent event);
        public void onLocationNotValid();
    }

    public interface onAlarmAdded{
        public void onAlarmAdded(String eventId, long rawAlarmTime, String label);
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    private Controller() {
        mGetLocationData = new GetLocationData();
        mGetCalendarData = new GetCalendarData();
        mDataStorage = new DataStorage();
        mWakeUpCalculation = new WakeUpCalculation();
    }

//    public void showSelectedEvent(String id, String title, long startTime, String endTime, String location, String calendarName){
//        mAlarmDataFragment.setEventData(id, title, startTime, endTime, location, calendarName);
//    }


    public void getEventsFromCalendars (Context context, List<String> selectedCalendarIds, CalendarCallback callback){
        mGetCalendarData.getEventsFromCalendar(context, selectedCalendarIds, callback);
    }

    public void setCurrentLat(double lat) {
        mDataStorage.setCurrentLat(lat);
    }

    public double getCurrentLat() {
        return mDataStorage.getCurrentLat();
    }

    public void calculateWakeUp(long mOriginMinutes, long mDestinationMinutes, Controller.WakeUpDataCallback callback){
        mWakeUpCalculation.getWakeUpTime(mOriginMinutes, mDestinationMinutes, callback);
    }

    public double getCurrentLng() {
        return mDataStorage.getCurrentLng();
    }

    public void setCurrentLng(double lng) {
        mDataStorage.setCurrentLng(lng);
    }

    public void setDurationInTrafficText(String duration) {
        mDataStorage.setDurationInTrafficText(duration);
    }

    public String getDurationValue() {
        return mDataStorage.getDurationValue();
    }

    public void setDurationValue(String durationValue){
        mDataStorage.setDurationValue(durationValue);
    }

    public void setDurationInTrafficValue(String duration) {
        mDataStorage.setDurationInTrafficValue(duration);
    }

    public String getDurationInTrafficValue() {
        return mDataStorage.getDurationInTrafficValue();
    }

    public void getCurrentLocation(Context context, GoogleApiClient googleApiClient, LocationCallback callback) {
        mGetLocationData.getCurrentLocation(context, googleApiClient, callback);
    }

    public void getCalendarsList(Context context, CalendarCallback callback) {
        mGetCalendarData.getCalendarsList(context, callback);
    }

    public void getMatrixDuration(Context context, LatLng currentLocation, LatLng destination,
                                  String departureTime, String travelMode, GetDistanceMatrixData.DistanceMatrixCallback callback){
        mWakeUpCalculation.getDuration(context, currentLocation, destination, departureTime, travelMode, callback);
    }

    public long getRawStartingTime() {
        return mDataStorage.getRawStartingTime();
    }

    public void setRawStartingTime(long rawStartingTime) {
        mDataStorage.setRawStartingTime(rawStartingTime);
    }
}
