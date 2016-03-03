package com.example.obroshi.alarmclock.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.example.obroshi.alarmclock.controller.Controller;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetDirectionsData extends GetRawData {
    private String TAG = GetDirectionsData.class.getSimpleName();

    private Uri mDestinationUri;
    private Context mContext;

    public interface DurationCallback {
        public void onDurationReceived(String durationText, String durationValue);
    }

    public GetDirectionsData(Context context, LatLng origin, LatLng destination, long arrivalTime, String travel_mode) {
        super(null);
        mContext = context;
        if (travel_mode == null) {
            travel_mode = "driving";
        }
        createUri(origin, destination, arrivalTime, travel_mode);
    }

    private boolean createUri(LatLng origin, LatLng destination, long arrival_time, String travel_mode) {
        final String GOOGLE_MAPS_QUERY_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
        final String GOOGLE_API_KEY = "AIzaSyDHRObgKZb24znTTZJXpxX7pZilCj2WDQE";

        final String ORIGIN_PARAM = "origin";
        final String DESTINATION_PARAM = "destination";
        final String ARRIVAL_TIME_PARAM = "arrival_time";
        final String MODE_PARAM = "mode";  // traveling mode (car / walk/ bicycle / public transportation)
        final String KEY_PARAM = "key";

        //  Build the google api request according to:    https://developers.google.com/maps/documentation/directions/intro
        mDestinationUri = Uri.parse(GOOGLE_MAPS_QUERY_BASE_URL).buildUpon()
                .appendQueryParameter(ORIGIN_PARAM, String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude))
                .appendQueryParameter(DESTINATION_PARAM, String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude))
                .appendQueryParameter(ARRIVAL_TIME_PARAM, String.valueOf(arrival_time))
                .appendQueryParameter(MODE_PARAM, travel_mode)
                .appendQueryParameter(KEY_PARAM, GOOGLE_API_KEY)
                .build();
        return mDestinationUri != null;
    }

    public void execute(DurationCallback callback) {
        DownloadJsonData downloadJsonData = new DownloadJsonData(callback);
        Log.d(TAG, "Built URI = " + mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public void processResult(DurationCallback callback) {
        if (getDownloadStatus() != DownloadStatus.OK) {
            Log.d(TAG, "Error downloading raw file");
            return;
        }
        final String RESULT_STATUS = "status";
        final String RESULT_ROUTES = "routes";
        final String RESULT_LEGS = "legs";
        final String RESULT_DURATION = "duration";
        final String RESULT_DURATION_VALUE = "value";
        final String RESULT_DURATION_TEXT = "text";
        final String RESULT_STEPS = "steps";
        final String RESULT_STEP_DURATION_VALUE = "value";

        try {
            JSONObject jsonData = new JSONObject(getData());
            if (!jsonData.getString(RESULT_STATUS).equals("OK")) {
                return;
            }
            String durationText = "";
            String durationValue = "";
            long totalDuration = 0;
            int totalSteps = 0;


            JSONArray routes = jsonData.optJSONArray(RESULT_ROUTES);
            for (int i = 0; i < routes.length(); i++) {
                JSONArray legs = routes.getJSONObject(i).getJSONArray(RESULT_LEGS);
                for (int j = 0; j < legs.length(); j++) {
                    JSONObject duration = legs.getJSONObject(j).getJSONObject(RESULT_DURATION);
                    durationValue = duration.getString(RESULT_DURATION_VALUE);
                    durationText = duration.getString(RESULT_DURATION_TEXT);
                    JSONArray steps = legs.getJSONObject(j).getJSONArray(RESULT_STEPS);
                    for (int k = 0; k < steps.length(); k++) {
                        JSONObject stepDuration = steps.getJSONObject(k).getJSONObject(RESULT_DURATION);
                        totalDuration += Long.valueOf(stepDuration.getString(RESULT_STEP_DURATION_VALUE));
                        totalSteps++;
                    }
                }
            }
            Log.d(TAG, "Duration: " + durationText + " which are " + durationValue + " seconds. Steps: " + totalSteps + " Total duration: " + totalDuration);
            callback.onDurationReceived(durationText, durationValue);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Error processing json data");
        }
    }

    public class DownloadJsonData extends DownloadRawData {

        private DurationCallback mCallback;

        public DownloadJsonData(DurationCallback callback) {
            this.mCallback = callback;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            processResult(mCallback);
        }

        @Override
        protected String doInBackground(String... params) {
            return super.doInBackground(params);
        }
    }
}
