package com.example.obroshi.alarmclock.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.obroshi.alarmclock.R;
import com.example.obroshi.alarmclock.controller.Controller;
import com.example.obroshi.alarmclock.model.CalendarEvent;
import com.example.obroshi.alarmclock.model.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Controller.onEventSelectedListener, Controller.onAlarmAdded {

    private final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private Controller.LocationCallback mLocationCallback;
    private Fragment mFragment;
    private FloatingActionButton mFab;
    private FragmentManager mFragmentManager;

    final int REQUEST_CODE_ASK_LOCATION_PERMISSIONS = 111;
    final int REQUEST_CODE_ASK_READ_CALENDAR_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        mFragment = new AlarmsListFragment();
        ft.add(R.id.container, mFragment, "AlarmsListFragment")
                .commit();
        mFab = (FloatingActionButton) findViewById(R.id.addAlarmFab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                mFragment = new EventsListFragment();
                ft.replace(R.id.container, mFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (mGoogleApiClient == null) {
            // Create a GoogleApiClient instance
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mLocationCallback = new Controller.LocationCallback() {
            @Override
            public void onCurrentLocationReceived(double lat, double lng) {
                Controller.getInstance().setCurrentLat(lat);
                Controller.getInstance().setCurrentLng(lng);
                showCalendarPermissionsPopUp();
            }
        };
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    public void showLocationPermissionsPopUp() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_ASK_LOCATION_PERMISSIONS);
    }

    private void showCalendarPermissionsPopUp() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_CALENDAR},
                REQUEST_CODE_ASK_READ_CALENDAR_PERMISSIONS);
    }


    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            Log.d(TAG, "Successfully disconnected from Google Play Services");
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Successfully Connected to Google Play Services");
        if (mGoogleApiClient.isConnected()) {
            showLocationPermissionsPopUp();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_READ_CALENDAR_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Calendar permissions granted");
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    mFragment = new EventsListFragment();
//                    if (fragmentManager.findFragmentByTag("EventsListFragment") == null)
//                        fragmentManager.beginTransaction().add(R.id.container, mFragment, "EventsListFragment").commitAllowingStateLoss();
                } else {
                    Log.d(TAG, "Calendar permissions denied, request popup is shown");
                    showCalendarPermissionsPopUp();
                }
                return;
            case REQUEST_CODE_ASK_LOCATION_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permissions granted");
                    Controller.getInstance().getCurrentLocation(getBaseContext(), mGoogleApiClient, mLocationCallback);
                } else {
                    Log.d(TAG, "Location permissions denied, request popup is shown");
                    showLocationPermissionsPopUp();
                }
                return;
            default:
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection to Google Play Services suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to Connected to Google Play Services");
    }

    @Override
    public void onEventSelected(String id, String title, String monthShortName, String dayInMonth, String startTime ,String endTime, long rawStartingTime, String location, String calendarName, int calendarColor) {
        mFragment = new AlarmDataFragment();
        Bundle args = new Bundle();
        args.putString(CalendarContract.Events._ID, id);
        args.putString(CalendarContract.Events.TITLE, title);
        args.putString(Constants.MONTH_IN_YEAR, monthShortName);
        args.putString(Constants.DAY_IN_MONTH, dayInMonth);
        args.putString(CalendarContract.Events.DTSTART, startTime);
        args.putString(CalendarContract.Events.DTEND, endTime);
        args.putLong(AlarmDataFragment.RAW_STARTING_TIME, rawStartingTime);
        args.putString(CalendarContract.Events.EVENT_LOCATION, location);
        args.putString(CalendarContract.Events.CALENDAR_DISPLAY_NAME, calendarName);
        args.putInt(CalendarContract.Events.CALENDAR_COLOR, calendarColor);
        mFragment.setArguments(args);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.container, mFragment).addToBackStack(null).commit();
    }

    @Override
    public void onLocationNotValid() {
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        mFragment = new SearchAddressFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).addToBackStack("SearchAddressFragment").commit();

//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.d(TAG, "Place: " + place.getName());
//
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.d(TAG, "An error occurred: " + status);
//            }
//        });
    }

    @Override
    public void onAlarmAdded(String time, String label) {
        mFragment = new AlarmsListFragment();
        Bundle args = new Bundle();
        args.putString(AlarmsListFragment.ALARM_TIME, time);
        args.putString(AlarmsListFragment.LABEL, label);
        mFragment.setArguments(args);
//        if (mFragmentManager.findFragmentByTag("AlarmsListFragment") == null)
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mFragmentManager.findFragmentByTag("AlarmsListFragment") != null)
            ft.replace(R.id.container, mFragment).commit();
        else {

        }
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        double lat = location.getAltitude();
//        double lng = location.getLongitude();
//        Log.d(TAG, "Location updated: " + lat + ", " + lng);
//        Toast.makeText(MainActivity.this,"Location updated: " + lat + ", " + lng, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
}
