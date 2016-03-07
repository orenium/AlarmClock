package com.example.obroshi.alarmclock.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.obroshi.alarmclock.R;
import com.example.obroshi.alarmclock.controller.Controller;
import com.example.obroshi.alarmclock.model.CalendarEvent;

public class AddAlarmActivity extends AppCompatActivity implements Controller.onEventSelectedListener, Controller.onAlarmAdded{

    private final String TAG = AddAlarmActivity.class.getSimpleName();
    private Fragment mFragment;
    private FragmentManager mFragmentManager;

    public static final String EVENT_ID = "eventID";
    public static final String KEY_RAW_ALARM_TIME = "rawAlarmTime";
    public static final String KEY_ALARM_LABEL = "alarmLabel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        mFragment = new EventsListFragment();
        ft.add(R.id.container, mFragment, "EventsListFragment").commit();
    }


    @Override
    public void onEventSelected(CalendarEvent event) {
        mFragment = AlarmDataFragment.getFragment(event);
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
    public void onAlarmAdded(String eventId, long rawAlarmTime, String label) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_RAW_ALARM_TIME, rawAlarmTime);
        returnIntent.putExtra(EVENT_ID, eventId);
        returnIntent.putExtra(KEY_ALARM_LABEL, label);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
