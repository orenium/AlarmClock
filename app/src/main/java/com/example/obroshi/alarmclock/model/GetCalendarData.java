package com.example.obroshi.alarmclock.model;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.obroshi.alarmclock.controller.Controller;

import java.util.ArrayList;
import java.util.List;

public class GetCalendarData {

    private final String TAG = GetCalendarData.class.getSimpleName();
    List<CalendarEvent> mEventsList;


    public void getCalendarsList(Context context, Controller.CalendarCallback callback) {
        ArrayList<AppCalendar> calendarsList = new ArrayList<>();

        String[] projection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR};

        ContentResolver calResolver = context.getContentResolver();
        int hasReadCalendarPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR);
        if (hasReadCalendarPermissions == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = calResolver.query(CalendarContract.Calendars.CONTENT_URI,
                    projection,
                    CalendarContract.Calendars.VISIBLE + " =1",
                    null,
                    CalendarContract.Calendars._ID + " ASC");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int idColumn = cursor.getColumnIndex(CalendarContract.Calendars._ID);
                    int nameColumn = cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
                    int colorColumns = cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR);
                    do {
                        long id = Long.valueOf(cursor.getString(idColumn));
                        String displayName = cursor.getString(nameColumn);
                        int calendarColor = cursor.getInt(colorColumns);
                        Log.d(TAG, "Calendar id: " + id + " display name: " + displayName);
                        calendarsList.add(new AppCalendar(id, displayName, calendarColor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                callback.onCalendarsListReceived(calendarsList);
            }
        }
    }

    public void getEventsFromCalendar(Context context,
                                      List<String> calendarIds,
                                      Controller.CalendarCallback callback) {
        List<CalendarEvent> eventsList = new ArrayList<>();
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
                CalendarContract.Events.RRULE,
                CalendarContract.Events.CALENDAR_DISPLAY_NAME};

//        String[] selectionArg = new String[]{TextUtils.join(", ", calendarIds)};
        String selection = CalendarContract.Events.CALENDAR_ID + " IN ("+ TextUtils.join(", ", calendarIds) +")";
        Log.d(TAG, "Events query calendars Id: " + selection);
        String[] selectionArg = null;

        // Read calendar permissions
        int hasReadCalendarPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR);
        if (hasReadCalendarPermissions == PackageManager.PERMISSION_GRANTED) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
                    projection,
                    selection,
                    selectionArg,
                    CalendarContract.Events.DTSTART + " ASC",
                    null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        // Skipping all day events
                        int isAllDay = cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.ALL_DAY));
                        if (isAllDay != 1) {   // 1 is the value for all day event
                            long startTime = Long.valueOf(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
                            // check for only future events
                            Long currentTime = System.currentTimeMillis();
                            if (startTime > currentTime) {
                                String cal_id = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
                                String event_id = cursor.getString(cursor.getColumnIndex(CalendarContract.Events._ID));
                                String title = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
                                int calendarColor = cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_COLOR));
                                int status = cursor.getInt(cursor.getColumnIndex(CalendarContract.Events.SELF_ATTENDEE_STATUS));
                                // check if the event's data is valid
                                if ((event_id != null) && (title != null) && (startTime > 0) && (status != CalendarContract.Events.STATUS_CANCELED)) {
                                    String endTime = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTEND));

                                    String rRule = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RRULE));

                                    String location = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                                    String description = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                                    String CalendarName = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.CALENDAR_DISPLAY_NAME));
//                                    Log.d(TAG, "Calendar id: " + cal_id + " Event id: " + event_id + ": " + title + " status: " + status);
                                    CalendarEvent event = new CalendarEvent(cal_id, event_id, title, startTime, endTime, location, description, CalendarName, calendarColor);
                                    eventsList.add(event);
                                } else {
                                    Log.d(TAG, "Skipped event: cal_id: " + cal_id + " event_id: " + event_id
                                            + " title: " + title + " start time: " + startTime + " status: " + status);
                                }
                            }
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                Log.d(TAG, eventsList.size() + " valid events found");
            }
        }
        mEventsList = eventsList;
        callback.onDataReceived(eventsList);
    }
}
