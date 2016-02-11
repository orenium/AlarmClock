package com.example.obroshi.alarmclock;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = MainActivity.class.getSimpleName();

    private ArrayList<CalendnarEvent> mEventsList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastKnownLocation;

    private int mhasReadCalendarPermmision;
    private int mHasLocationPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (mGoogleApiClient == null) {
            // Create a GoogleApiClient instance
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.eventsRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.hasFixedSize();

        checkPermissions();
        getCalendarsList();
        getEventsFromCalendar("1");  //TODO: Change the calendar_id to the user's selection (1 is for google calendar on my device

        adapter = new EventsAdapter(MainActivity.this, mEventsList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "Item " + position + " was clicked");
                CalendnarEvent event = mEventsList.get(position);
                Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
                intent.putExtra(CalendarContract.Events._ID, event.getEvent_ID());
                intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());
                intent.putExtra(CalendarContract.Events.DTSTART, event.getStartingTime());
                intent.putExtra(CalendarContract.Events.DTEND, event.getEndingTime());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDesctiption());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, "Item " + position + " long Clicked");
            }
        }));
    }


    public ArrayList<CalendnarEvent> getEventsFromCalendar(String calendar_id) {
        mEventsList = new ArrayList<CalendnarEvent>();

        String[] projection = new String[]{
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.DESCRIPTION};


        //  this shows the events FROM A SPECIFIC calendar
        // TODO: this use only the google calendar for tests (Dynamic Alarm Clock Calendar)
        String selection = CalendarContract.Events.CALENDAR_ID + "=?";
        String[] selectionArg = new String[]{calendar_id};

        ContentResolver contentResolver = getContentResolver();
        int hasReadCalendarPermmision = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALENDAR);
        if (hasReadCalendarPermmision == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
                    projection,
                    selection,
                    selectionArg,
                    null,
                    null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    do {
                        // Skipping all day events
                        int isAllDay = cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ALL_DAY));
                        if (isAllDay != 1) {   // 1 is the value for all day event
                            String cal_id = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
                            String event_id = cursor.getString(cursor.getColumnIndex(CalendarContract.Events._ID));
                            String title = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
                            String startTime = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART));
                            String endTime = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTEND));
                            String location = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                            String description = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                            Log.d(TAG, "Calender id: " + cal_id + " Event id: " + event_id + ": " + title);
                            CalendnarEvent event = new CalendnarEvent(cal_id, event_id, title, startTime, endTime, location, description);
                            mEventsList.add(event);
                        }
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        }
        return mEventsList;
    }

    // If permissions not granted, show a request permissions popup
    private void checkPermissions() {
        checkCalendarPermissions();
    }

    private void checkCalendarPermissions() {

        // Read calendar permissions
        final int REQUEST_CODE_ASK_READ_CALENDAR_PERMISSIONS = 123;
        mhasReadCalendarPermmision = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALENDAR);
        if (mhasReadCalendarPermmision == PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CALENDAR)) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALENDAR},
//                        REQUEST_CODE_ASK_READ_CALENDAR_PERMISSIONS);
//                return;
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    REQUEST_CODE_ASK_READ_CALENDAR_PERMISSIONS);
        }
        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        // Get Location permissions
        final int REQUEST_CODE_ASK_LOCATION_PERMISSIONS = 111;
        mHasLocationPermissions = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (mHasLocationPermissions == PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                        REQUEST_CODE_ASK_LOCATION_PERMISSIONS);
//                return;
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_LOCATION_PERMISSIONS);
        }
    }

    public void getCalendarsList() {
        String[] projection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE};

        ContentResolver calResolver = getContentResolver();
        if (mhasReadCalendarPermmision == PackageManager.PERMISSION_GRANTED) {
            Cursor calCursor = calResolver.query(CalendarContract.Calendars.CONTENT_URI,
                    projection,
                    CalendarContract.Calendars.VISIBLE + " =1",
                    null,
                    CalendarContract.Calendars._ID + " ASC");
            if (calCursor.moveToFirst()) {
                do {
                    long id = calCursor.getLong(0);
                    String displayName = calCursor.getString(1);
                    String accountType = calCursor.getString(3);
                    Log.d(TAG, "Calendar id: " + id + " display name: " + displayName + " type: " + accountType);
                } while (calCursor.moveToNext());
            }
            calCursor.close();
        }
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Successfully Connected to Google Play Services");
        checkLocationPermissions();
        if (mGoogleApiClient.isConnected()) {
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastKnownLocation != null) {
                Log.d(TAG, "Location found: LAT: " + String.valueOf(mLastKnownLocation.getLatitude())
                        + " LNG: " + String.valueOf(mLastKnownLocation.getLongitude()));
            } else
                Log.d(TAG, "Unable to get last location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to Connected to Google Play Services");
    }
}
