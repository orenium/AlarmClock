package com.example.obroshi.alarmclock.model;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.obroshi.alarmclock.controller.Controller;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WakeUpCalculation {

    private final String TAG = WakeUpCalculation.class.getSimpleName();
    private long mRawStartingTime;

    public static final int ERROR_ALARM_IS_IN_PAST = 123;
    public static final int UNABLE_TO_FIND_DURATION = 111;

    public void getDuration(Context context, LatLng currentLocation, LatLng destination,
                            String departureTime, String travelMode, GetDistanceMatrixData.DistanceMatrixCallback callback) {
        GetDistanceMatrixData getDistanceMatrixData = new GetDistanceMatrixData(context, currentLocation,
                destination, departureTime, travelMode);
        getDistanceMatrixData.execute(callback);
    }

    public long calculateETA(int timeInMinutes) {
        mRawStartingTime = Long.valueOf(Controller.getInstance().getRawStartingTime());
        long eta = ((mRawStartingTime - (timeInMinutes * 60) * 1000));
        Log.d(TAG, "ETA in unix time is: " + eta);
        return eta;
    }

    public void getWakeUpTime(long originMinutes, long destinationMinutes, Controller.WakeUpDataCallback callback) {
        long wakeupTime;
        long duration_value = Long.valueOf(Controller.getInstance().getDurationInTrafficValue());

        if (duration_value != 0) {
            // If user user didn't supply and destinationTime, the timeAtDestination will be the event's starting time
            mRawStartingTime = Controller.getInstance().getRawStartingTime();
            if (destinationMinutes > 0) {
                wakeupTime = (mRawStartingTime - (1000 * (duration_value + 60 * (originMinutes + destinationMinutes))));
            } else {
                wakeupTime = (mRawStartingTime - (1000 * (duration_value + (originMinutes * 60))));
            }
            Date date = new Date(wakeupTime);
//            DateTime dateTIme = new DateTime(wakeupTime);
            //check if wakeUpTime is in the future
            if (date.after(new Date(System.currentTimeMillis()))) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat sdfHours = new SimpleDateFormat("HH", Locale.getDefault());
                final String wakeUpHour = sdfHours.format(date);
                SimpleDateFormat sdfMinutes = new SimpleDateFormat("mm", Locale.getDefault());
                final String wakeUpMinutes = sdfMinutes.format(date);
                Log.d(TAG, "WAKEUP TIME SET TO: " + wakeUpHour + ":" + wakeUpMinutes);

                callback.onWakeUpTimeReceived(wakeupTime, sdf.format(date), wakeUpHour, wakeUpMinutes);

            } else {   // If the wake up Time is not at the future
                callback.onFailed(ERROR_ALARM_IS_IN_PAST);
            }
        } else {
            callback.onFailed(UNABLE_TO_FIND_DURATION);
        }
    }
}
