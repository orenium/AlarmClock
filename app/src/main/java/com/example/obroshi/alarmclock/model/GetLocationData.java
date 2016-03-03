package com.example.obroshi.alarmclock.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.obroshi.alarmclock.controller.Controller;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GetLocationData {

    private final String TAG = GetLocationData.class.getSimpleName();

    public void getCurrentLocation(Context context, GoogleApiClient googleApiClient, Controller.LocationCallback callback) {
        int hasLocationPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocationPermissions == PackageManager.PERMISSION_GRANTED) {
            Location mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (mLastKnownLocation != null) {
                Log.d(TAG, "Current location found: " + String.valueOf(mLastKnownLocation.getLatitude())
                        + " , " + String.valueOf(mLastKnownLocation.getLongitude()));
                callback.onCurrentLocationReceived(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            } else {
                Log.d(TAG, "Unable to get last location");
            }
        }
    }
}
