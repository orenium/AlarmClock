package com.example.obroshi.alarmclock.model;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.obroshi.alarmclock.controller.Controller;

import java.util.ArrayList;
import java.util.List;

public class EventsListLoader extends AsyncTaskLoader<List<CalendarEvent>> {

    private static final String TAG = EventsListLoader.class.getSimpleName();
    private List<CalendarEvent> mEventsList;
    private ContentResolver mContentResolver;
    private Cursor mCursor;
    private List<String> mSelectedCalendarsList;
    private Context mContext;


    public EventsListLoader(Context context, Uri uri, ContentResolver contentResolver, List<String> selectedCalendarsList) {
        super(context);
        mContext = context;
        mContentResolver = contentResolver;
        mSelectedCalendarsList = selectedCalendarsList;
    }


    @Override
    public List<CalendarEvent> loadInBackground() {
        List<CalendarEvent> events = new ArrayList<>();
        String[] projection = new String[]{
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.SELF_ATTENDEE_STATUS,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.CALENDAR_COLOR,
                CalendarContract.Events.CALENDAR_DISPLAY_NAME};

        StringBuilder selection = new StringBuilder();
        selection.append(CalendarContract.Events.CALENDAR_DISPLAY_NAME + "=?");
        String[] selectionArg = new String[mSelectedCalendarsList.size()];
        selectionArg = mSelectedCalendarsList.toArray(selectionArg);
        if (mSelectedCalendarsList.size() > 1) {
            for (int i = 0; i < mSelectedCalendarsList.size() - 1; i++) {
                selection.append(" OR " + CalendarContract.Events.CALENDAR_DISPLAY_NAME + "=?");
                selectionArg[i] = mSelectedCalendarsList.get(i);
            }
        }

        // Read calendar permissions
        int hasReadCalendarPermissions = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR);
        if (hasReadCalendarPermissions == PackageManager.PERMISSION_GRANTED) {
            mContentResolver = mContext.getContentResolver();
            mCursor = mContentResolver.query(CalendarContract.Events.CONTENT_URI,
                    projection,
                    selection.toString(),
                    selectionArg,
                    CalendarContract.Events.DTSTART + " ASC",
                    null);

            if (mCursor != null) {
                if (mCursor.moveToFirst()) {
                    do {
                        // Skipping all day events
                        int isAllDay = mCursor.getInt(mCursor.getColumnIndex(CalendarContract.Events.ALL_DAY));
                        if (isAllDay != 1) {   // 1 is the value for all day event
                            String cal_id = mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
                            String event_id = mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events._ID));
                            String title = mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.TITLE));
                            long startTime = Long.valueOf(mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.DTSTART)));
                            int status = mCursor.getInt(mCursor.getColumnIndex(CalendarContract.Events.SELF_ATTENDEE_STATUS));

                            // check for only future events
                            Long currentTime = System.currentTimeMillis();
                            if (startTime > currentTime) {
                                // check if the event's data is valid
                                if ((event_id != null) && (title != null) && (startTime > 0) && (status != CalendarContract.Events.STATUS_CANCELED)) {
                                    String endTime = mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.DTEND));
                                    String location = mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                                    String description = mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                                    String CalendarName = mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.CALENDAR_DISPLAY_NAME));
                                    int calendarColor = mCursor.getInt(mCursor.getColumnIndex(CalendarContract.Events.CALENDAR_COLOR));
//                                    Log.d(TAG, "Calendar id: " + cal_id + " Event id: " + event_id + ": " + title + " status: " + status);
                                    CalendarEvent event = new CalendarEvent(cal_id, event_id, title, startTime, endTime, location, description, CalendarName, calendarColor);
                                    events.add(event);
                                } else {
                                    Log.d(TAG, "Skipped event: cal_id: " + cal_id + " event_id: " + event_id
                                            + " title: " + title + " start time: " + startTime + " status: " + status);
                                }
                            }
                        }
                    } while (mCursor.moveToNext());
                }
                mCursor.close();
                Log.d(TAG, mEventsList.size() + " valid events found");
            }
        }
//        mCallback.onDataReceived(events);
        return events;
    }

    @Override
    public void deliverResult(List<CalendarEvent> events) {
        if (isReset()) {
            if (events != null) {
                mCursor.close();
            }
        }
        List<CalendarEvent> oldEventsList = mEventsList;
        if (mEventsList == null || mEventsList.size() == 0) {
            Log.d(TAG, "++++   No Data returned ++++");
        }
        mEventsList = events;
        if (isStarted()) {
            super.deliverResult(events);
        }

        if (oldEventsList != null || oldEventsList != events) {
            mCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mEventsList != null) {
            deliverResult(mEventsList);
        }
        if (takeContentChanged() || mEventsList == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }


    @Override
    protected void onReset() {
        onStopLoading();
        if (mCursor != null) {
            mCursor.close();
        }
        mEventsList = null;
    }

    @Override
    public void onCanceled(List<CalendarEvent> events) {
        super.onCanceled(events);
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }
}



