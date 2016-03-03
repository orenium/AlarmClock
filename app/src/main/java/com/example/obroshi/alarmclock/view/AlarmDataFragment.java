package com.example.obroshi.alarmclock.view;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.obroshi.alarmclock.R;
import com.example.obroshi.alarmclock.controller.Controller;
import com.example.obroshi.alarmclock.model.Constants;
import com.example.obroshi.alarmclock.model.GetDistanceMatrixData;
import com.google.android.gms.maps.model.LatLng;
import com.vstechlab.easyfonts.EasyFonts;

import org.joda.time.DateTime;

import java.util.List;

public class AlarmDataFragment extends Fragment {

    private static final String TAG = AlarmDataFragment.class.getSimpleName();
    public static final String RAW_STARTING_TIME = "rawStartingTime";

    private LinearLayout mEventInfoLayout;
    private String mEventID;
    private TextView mTitle;
    private TextView mFormattedStartTime;
    private TextView mMonthInYear;
    private TextView mDayInMonth;
    private TextView mEndTime;
    private TextView mLocation;
    private View mDivider;
    private int mOriginMinutes;
    private int mDestinationMinutes;
    private TextView mLocationMsg;
    private TextView mWakeUpTime;
    private TextView mDetailsWakeUp;
    private TextView mDetailsLeaveHome;
    private TextView mDetailsDuration;
    private TextView mDetailsLeaveETA;
    private TextView mDetailsEventStart;
    private LinearLayout mDetailsLayout;
    private ImageView mAddAlarmBtn;


    private Controller.onEventSelectedListener mSelectedListener;
    private double mDestinationLatitude;
    private double mDestinationLongitude;

    private GetDistanceMatrixData.DistanceMatrixCallback callback;

    private long mRawStartingTime;
    private boolean mIsAddressValid = false;


    // Empty Constructor
    public AlarmDataFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm_data, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mEventInfoLayout = (LinearLayout) view.findViewById(R.id.eventInfoLayout);
        mTitle = (TextView) view.findViewById(R.id.eventTitle);
        mMonthInYear = (TextView) view.findViewById(R.id.monthInYear);
        mDayInMonth = (TextView) view.findViewById(R.id.dayInMonth);
        mDivider = view.findViewById(R.id.separatorView);
        mFormattedStartTime = (TextView) view.findViewById(R.id.startTime);
        mEndTime = (TextView) view.findViewById(R.id.endTime);
        mLocation = (TextView) view.findViewById(R.id.location);
        mLocationMsg = (TextView) view.findViewById(R.id.locationErrorMsg);
        mWakeUpTime = (TextView) view.findViewById(R.id.wakeUpTime);
        mDetailsLayout = (LinearLayout) view.findViewById(R.id.detailsLayout);
        mDetailsWakeUp = (TextView) view.findViewById(R.id.detailsWakeUp);
        mDetailsLeaveHome = (TextView) view.findViewById(R.id.detailsLeaveHome);
        mDetailsDuration = (TextView) view.findViewById(R.id.detailsDuration);
        mDetailsLeaveETA = (TextView) view.findViewById(R.id.detailsETA);
        mDetailsEventStart = (TextView) view.findViewById(R.id.detailsEventStart);
        mAddAlarmBtn = (ImageView) view.findViewById(R.id.addAlarmImage);

        mEventInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.valueOf(mEventID));
                Intent openEventIntent = new Intent(Intent.ACTION_VIEW);
                openEventIntent.setData(uri);
                startActivity(openEventIntent);
            }
        });
        callback = new GetDistanceMatrixData.DistanceMatrixCallback() {
            @Override
            public void onDurationReceived(final String durationText, String durationValue, final String durationInTrafficText, final String durationInTrafficValue) {
                if (durationText != null) {
                    Controller.getInstance().setDurationInTrafficText(durationInTrafficText);
                    Controller.getInstance().setDurationInTrafficValue(durationInTrafficValue);
                    Controller.getInstance().setDurationValue(durationValue);

                    Controller.getInstance().calculateWakeUp(mOriginMinutes, mDestinationMinutes, new Controller.WakeUpDataCallback() {
                        @Override
                        public void onWakeUpTimeReceived(long rawWakeupTime, String formattedWakupTime, final String hours, final String minutes) {
//                            Toast.makeText(getContext(), "Duration: " + durationText + " (" + durationInTrafficText + " with traffic)", Toast.LENGTH_LONG).show();
                            mWakeUpTime.setText(formattedWakupTime);
                            mWakeUpTime.setVisibility(View.VISIBLE);
                            mAddAlarmBtn.setVisibility(View.VISIBLE);
                            mAddAlarmBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, mTitle.getText());
                                    intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(hours));
                                    intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(minutes));
                                    startActivity(intent);
                                }
                            });
//                            mLocationMsg.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    double lat = Controller.getInstance().getCurrentLat();
//                                    double lng = Controller.getInstance().getCurrentLat();
//                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                                            Uri.parse("geo:" + lat + "," + lng + "?q=" + mLocation));
//                                    startActivity(intent);
//                                }
//                            });
                            setDetails(rawWakeupTime, formattedWakupTime, durationInTrafficValue, durationInTrafficText, mOriginMinutes);
                        }

                        @Override
                        public void onFailed(int error) {
                            switch (error) {
                                case Constants.ERROR_ALARM_IS_IN_PAST:
                                    Log.d(TAG, "Alarm was set incorrectly. please check user inputs.");
                                    Toast.makeText(getContext(), "Only future alarms can be set", Toast.LENGTH_SHORT).show();
                                    break;
                                case Constants.UNABLE_TO_FIND_DURATION:
                                    Log.d(TAG, "There was an error getting current location");
                                    Toast.makeText(getContext(), "Unable to get duration, please try again", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                            }
                            mDetailsLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        };
    }

    private void setDetails(long rawWakeupTime, String wakeUpTime, String durationValue, String durationText, int originMinutes) {
        mDetailsWakeUp.setText("Wake Up at " + wakeUpTime);
        DateTime wakeupTime = new DateTime(rawWakeupTime);
        int hours = wakeupTime.plusMinutes(originMinutes).getHourOfDay();
        int minutes = wakeupTime.plusMinutes(originMinutes).getMinuteOfHour();
        mDetailsLeaveHome.setText("Leave home at " + (hours < 10 ? "0" + hours : hours)
                + ":" + (minutes < 10 ? "0" + minutes : minutes));
        mDetailsDuration.setText("Driving duration is " + durationText);
        DateTime eta = new DateTime(rawWakeupTime + originMinutes * 60 * 1000 + Integer.valueOf(durationValue) * 1000);
        hours = eta.getHourOfDay();
        minutes = eta.getMinuteOfHour();
        mDetailsLeaveETA.setText("ETA: " + (hours < 10 ? "0" + hours : hours)
                + ":" + (minutes < 10 ? "0" + minutes : minutes));
        mDetailsEventStart.setText("Event starts at " + mFormattedStartTime.getText());
        mDetailsLayout.setVisibility(View.VISIBLE);
    }

    private void calculateAlarm() {
        if (mIsAddressValid) {
            // Get current location lat lng
            LatLng currentLocationLatLng = new LatLng(
                    Controller.getInstance().getCurrentLat(),
                    Controller.getInstance().getCurrentLng());
            LatLng destinationLatLng = new LatLng(mDestinationLatitude, mDestinationLongitude);
            String departure_time = "";

            // Get user settings from shared prefs
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            mOriginMinutes = sharedPref.getInt(Constants.ORIGIN_TIME, 0);
            mDestinationMinutes = sharedPref.getInt(Constants.DESTINATION_TIME, 0);

            departure_time = calculateDepartureTime(mOriginMinutes, mDestinationMinutes);
            Controller.getInstance().getMatrixDuration(getContext(), currentLocationLatLng,
                    destinationLatLng,
                    departure_time,
                    null, callback);


        } else { // if the event's address is not valid
            mWakeUpTime.setVisibility(View.INVISIBLE);
            mAddAlarmBtn.setVisibility(View.INVISIBLE);
            mDetailsLayout.setVisibility(View.INVISIBLE);
            mLocationMsg.setText("Could not use this event's location");
            mLocationMsg.setVisibility(View.VISIBLE);
//            Toast.makeText(getContext(), "Could not use this event's location", Toast.LENGTH_SHORT).show();
        }
    }

    public String calculateDepartureTime(int originMinutes, int destinationMinutes) {
        long departureTime = (mRawStartingTime - ((originMinutes + destinationMinutes) * 60 * 1000));
        return String.valueOf(departureTime);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            mEventID = args.getString(CalendarContract.Events._ID);
            mTitle.setText(args.getString(CalendarContract.Events.TITLE));
            mMonthInYear.setText(args.getString(Constants.MONTH_IN_YEAR));
            mDayInMonth.setText(args.getString(Constants.DAY_IN_MONTH));
            mFormattedStartTime.setText(args.getString(CalendarContract.Events.DTSTART));
            String endTime = args.getString(CalendarContract.Events.DTEND);
            if (endTime.isEmpty()) {
                mEndTime.setVisibility(View.GONE);
            } else {
                mEndTime.setVisibility(View.VISIBLE);
                mEndTime.setText(" - " + endTime);
            }
            mRawStartingTime = args.getLong(RAW_STARTING_TIME);
            String location = args.getString(CalendarContract.Events.EVENT_LOCATION);
            mDivider.setBackgroundColor(args.getInt(CalendarContract.Events.CALENDAR_COLOR));
            mIsAddressValid = getLatLongFromAddress(location);
            if (!location.isEmpty()) {
                mLocation.setText(location);
            } else {
                mLocation.setVisibility(View.GONE);
//                mLocation.setTextColor(Color.RED);
//                showGooglePlacesDialog();
            }
//            mCalendarName.setText("(" + args.getString(CalendarContract.Events.CALENDAR_DISPLAY_NAME) + ")");
        }
        Controller.getInstance().setRawStartingTime(String.valueOf(mRawStartingTime));
    }

    @Override
    public void onResume() {
        super.onResume();
        calculateAlarm();
    }

    public void showGooglePlacesDialog() {
        mSelectedListener.onLocationNotValid();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        mSelectedListener = (Controller.onEventSelectedListener) activity;
    }

    private boolean getLatLongFromAddress(String address) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                mDestinationLatitude = addresses.get(0).getLatitude();
                mDestinationLongitude = addresses.get(0).getLongitude();
                Log.d(TAG, "Event's location found: " + mDestinationLatitude + ",  " + mDestinationLongitude);
                return true;
            } else {
                Log.d(TAG, "Event's location was not found: (" + mTitle.getText() + ")");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
