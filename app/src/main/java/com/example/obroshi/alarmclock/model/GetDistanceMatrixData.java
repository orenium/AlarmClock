package com.example.obroshi.alarmclock.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GetDistanceMatrixData extends GetRawData {

    private String TAG = GetDistanceMatrixData.class.getSimpleName();
    private Uri mDestinationUri;
    private Context mContext;

    public interface DistanceMatrixCallback {
        public void onDurationReceived(String durationText, String durationValue, String durationInTrafficText, String durationInTrafficValue);
    }

    public GetDistanceMatrixData(Context context, LatLng origin, LatLng destination, String departureTime, String travel_mode) {
        super(null);
        mContext = context;
        if (travel_mode == null) {
            travel_mode = "driving";
        }
        createUri(origin, destination, departureTime, travel_mode);
    }

    private boolean createUri(LatLng origin, LatLng destination, String departureTime, String travel_mode) {
        final String GOOGLE_MAPS_QUERY_BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
        final String GOOGLE_API_KEY = "AIzaSyBuopsp7pmgOfLRiOJ_7qG81vha0PYuouI";

        final String ORIGIN_PARAM = "origins";
        final String DESTINATION_PARAM = "destinations";
        final String DEPARTURE_TIME_PARAM = "departure_time";
        final String MODE_PARAM = "mode";  // traveling mode (car (default)/ walk/ bicycle / public transportation)
        final String KEY_PARAM = "key";

        //  Build the google api request according to:    https://developers.google.com/maps/documentation/distance-matrix/intro
        mDestinationUri = Uri.parse(GOOGLE_MAPS_QUERY_BASE_URL).buildUpon()
                .appendQueryParameter(ORIGIN_PARAM, String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude))
                .appendQueryParameter(DESTINATION_PARAM, String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude))
                .appendQueryParameter(DEPARTURE_TIME_PARAM, departureTime)
                .appendQueryParameter(MODE_PARAM, travel_mode)
                .appendQueryParameter(KEY_PARAM, GOOGLE_API_KEY)
                .build();
        return mDestinationUri != null;
    }

    public void execute(DistanceMatrixCallback callback) {
        DownloadJsonData downloadJsonData = new DownloadJsonData(callback);
        Log.d(TAG, "Built URI = " + mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public void processResult(DistanceMatrixCallback callback) {
        if (getDownloadStatus() != DownloadStatus.OK) {
            Log.d(TAG, "Error downloading raw file");
            return;
        }

        String durationText = "";
        String durationValue = "";
        String durationInTrafficText = "";
        String durationInTrafficValue = "";
        final String RESULT_STATUS = "status";
        final String RESULT_ROWS = "rows";
        final String RESULT_ELEMENTS = "elements";
        final String RESULT_DURATION_IN_TRAFFIC = "duration_in_traffic";
        final String RESULT_DURATION_IN_TRAFFIC_VALUE = "value";
        final String RESULT_DURATION_IN_TRAFFIC_TEXT = "text";
        final String RESULT_DURATION = "duration";
        final String RESULT_DURATION_VALUE = "value";
        final String RESULT_DURATION_TEXT = "text";


        try {
            JSONObject jsonData = new JSONObject(getData());
            if (!jsonData.getString(RESULT_STATUS).equals("OK")) {
                return;
            }
            JSONArray rows = jsonData.getJSONArray(RESULT_ROWS);
            for (int i = 0; i < rows.length(); i++) {
                JSONArray elements = rows.getJSONObject(i).getJSONArray(RESULT_ELEMENTS);
                for (int j = 0; j < elements.length(); j++) {
                    JSONObject duration = elements.getJSONObject(j).getJSONObject(RESULT_DURATION);
                    durationValue = duration.getString(RESULT_DURATION_VALUE);
                    durationText = duration.getString(RESULT_DURATION_TEXT);
                    JSONObject durationInTraffic = elements.getJSONObject(j).getJSONObject(RESULT_DURATION_IN_TRAFFIC);
                    durationInTrafficValue = durationInTraffic.getString(RESULT_DURATION_IN_TRAFFIC_VALUE);
                    durationInTrafficText = durationInTraffic.getString(RESULT_DURATION_IN_TRAFFIC_TEXT);
                }
            }

            Log.d(TAG, "Duration: " + durationText + " which are " + durationValue + " seconds");
            Log.d(TAG, "Duration with traffic: " + durationInTrafficText + " which are " + durationInTrafficValue + " seconds");
            callback.onDurationReceived(durationText, durationValue , durationInTrafficText, durationInTrafficValue);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Error processing json data");
        }
    }

    public class DownloadJsonData extends GetRawData.DownloadRawData {

        private GetDistanceMatrixData.DistanceMatrixCallback mCallback;

        public DownloadJsonData(GetDistanceMatrixData.DistanceMatrixCallback callback) {
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
