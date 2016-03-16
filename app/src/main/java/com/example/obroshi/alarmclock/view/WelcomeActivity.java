package com.example.obroshi.alarmclock.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.obroshi.alarmclock.R;
import com.example.obroshi.alarmclock.model.Constants;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class WelcomeActivity extends AppCompatActivity {

    private final String TAG = WelcomeActivity.class.getSimpleName();
//    private Fragment mFragment;
//    private FragmentManager mFragmentManager;

    private RelativeLayout mWelcomeLayout;
    private LinearLayout mMorningLayout, mDestinationLayout;
    private boolean mHasTimes;

    private EditText mOriginMinutes;
    private EditText mDestinationMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        mWelcomeLayout = (RelativeLayout) findViewById(R.id.welcomeLayout);
        mMorningLayout = (LinearLayout) findViewById(R.id.welcomeWakeUpLayout);
        mDestinationLayout = (LinearLayout) findViewById(R.id.welcomeDestinationLayout);
        mOriginMinutes = (EditText) findViewById(R.id.originTime);
        mDestinationMinutes = (EditText) findViewById(R.id.destinationTime);
        TextView letsStart = (TextView) findViewById(R.id.welcomeStart);
        letsStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWelcomeLayout.setVisibility(View.GONE);
                mMorningLayout.setVisibility(View.VISIBLE);
                if (mMorningLayout.getVisibility() == View.VISIBLE) {
                    if (sharedPref.contains(Constants.ORIGIN_TIME)) {
                        mOriginMinutes.setText(String.valueOf(sharedPref.getInt(Constants.ORIGIN_TIME, 0)));
                    }
                    mOriginMinutes.requestFocus();
                } else {
                    Log.d(TAG, "View is not yet visible");
                }
            }
        });

        TextView setMorningTime = (TextView) findViewById(R.id.setTimes);
        setMorningTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((TextUtils.isDigitsOnly(mOriginMinutes.getText().toString()))) {
                    if (sharedPref.contains(Constants.DESTINATION_TIME)) {
                        mDestinationMinutes.setText(String.valueOf(sharedPref.getInt(Constants.DESTINATION_TIME, 0)));
                    }
                    int originTime = mOriginMinutes.getText().length() > 0 ? Integer.parseInt(mOriginMinutes.getText().toString()) : 0;
                    editor.putInt(Constants.ORIGIN_TIME, originTime);
                    editor.commit();
                    Log.d(TAG, "Origin time set to " + originTime + " minutes");
                    mMorningLayout.setVisibility(View.GONE);
                    mDestinationLayout.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "Invalid input");
                }
            }
        });

        TextView doneBtn = (TextView) findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isDigitsOnly(mDestinationMinutes.getText().toString())) {
                    int destinationTime = mDestinationMinutes.getText().length() > 0 ? Integer.parseInt(mDestinationMinutes.getText().toString()) : 0;
                    mHasTimes = true;

                    editor.putInt(Constants.DESTINATION_TIME, destinationTime);
                    editor.putBoolean(Constants.HAS_USER_TIMES, mHasTimes);
                    editor.commit();
                    Log.d(TAG, "Destination time set to " + destinationTime + " minutes");
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "Invalid input");
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        mHasTimes = sharedPref.getBoolean(Constants.HAS_USER_TIMES, false);
        if (mHasTimes) {
            Log.d(TAG, "This is not 1st launch, continuing to alarms list activity");
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//            Toast.makeText(this, "Has times: " + sharedPref.contains(Constants.HAS_USER_TIMES), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
