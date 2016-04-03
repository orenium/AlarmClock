package com.example.obroshi.alarmclock.view;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.obroshi.alarmclock.R;
import com.example.obroshi.alarmclock.controller.Controller;
import com.example.obroshi.alarmclock.model.AlertReceiver;
import com.example.obroshi.alarmclock.model.Constants;
import com.example.obroshi.alarmclock.model.MyAlarm;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.orm.query.Select;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private Controller.LocationCallback mLocationCallback;

    final int REQUEST_CODE_ASK_LOCATION_PERMISSIONS = 111;
    final int REQUEST_CODE_ASK_READ_CALENDAR_PERMISSIONS = 123;
    final int ADD_ALARM_ACTIVITY_REQUEST_CODE = 123;

    private RecyclerView mRecyclerView;
    private AlarmsAdapter mAdapter;
    private List<MyAlarm> myAlarmList = new ArrayList<>();

    private TextView mEmptyMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        boolean hasTimes = sharedPref.getBoolean(Constants.HAS_USER_TIMES, false);
        if (!hasTimes) {
            Log.d(TAG, "This is not 1st launch, continuing to alarms list activity");
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
//            Toast.makeText(this, "Has times: " + sharedPref.contains(Constants.HAS_USER_TIMES), Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.alarmsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.hasFixedSize();
        mAdapter = new AlarmsAdapter(MainActivity.this, myAlarmList);
        mRecyclerView.setAdapter(mAdapter);
        mEmptyMsg = (TextView) findViewById(R.id.noAlarmsMsg);

        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.addAlarmFab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                startActivityForResult(intent, ADD_ALARM_ACTIVITY_REQUEST_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_ALARM_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            String eventId = data.getStringExtra(AddAlarmActivity.EVENT_ID);
            long rawAlarmTime = data.getLongExtra(AddAlarmActivity.KEY_RAW_ALARM_TIME, 0L);
            String label = data.getStringExtra(AddAlarmActivity.KEY_ALARM_LABEL);
            MyAlarm alarm;
            if (!label.isEmpty()) {
                alarm = new MyAlarm(eventId, rawAlarmTime, label);
            } else {
                alarm = new MyAlarm(eventId, rawAlarmTime);
            }
            myAlarmList.add(alarm);
            alarm.save();    // save to DB :    http://satyan.github.io/sugar/getting-started.html
            mAdapter.notifyDataSetChanged();

        }
    }


    @Override
    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        loadAlarmsFromDb();
        super.onStart();
        setAlarm();
    }


    /*  Get all the items from the alarms.db
       Source:  https://guides.codepath.com/android/Clean-Persistence-with-Sugar-ORM
   */
    private void loadAlarmsFromDb() {
        if (myAlarmList == null) {
            myAlarmList = new ArrayList<>();
        } else {
            myAlarmList.clear();
        }
        long count = MyAlarm.count(MyAlarm.class);
        // get all alarms, sorted my Raw Alarm Time;
        List<MyAlarm> list = MyAlarm.listAll(MyAlarm.class, "m_raw_alarm_time");
        for (int i = 0; i < count; i++) {
            myAlarmList.add(list.get(i));
        }
        Log.d(TAG, count + " alarms found");
        if (myAlarmList.size() > 0) {
            mEmptyMsg.setVisibility(View.GONE);
        } else {
            mEmptyMsg.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
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

    public void setAlarm() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, 1,
                alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

    }

}
