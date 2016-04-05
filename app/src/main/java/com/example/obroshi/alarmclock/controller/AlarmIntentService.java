package com.example.obroshi.alarmclock.controller;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import com.example.obroshi.alarmclock.model.AlertReceiver;
import com.example.obroshi.alarmclock.model.MyAlarm;

import java.util.Calendar;
import java.util.List;

/**
 * This service (currently :) ) stops the pending alarm and recalculating (and set) the next first alarm
 */
public class AlarmIntentService extends IntentService {

    private static final String TAG = AlarmIntentService.class.getSimpleName();

    private static final String ACTION_RECALCULATE_ALARM = "com.example.obroshi.alarmclock.controller.action.RECALCULATE";

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    /**
     * Starts this service to recalculate the next alarm
     *
     * @see IntentService
     */
    public static void setNextAlarm(Context context) {
        Intent intent = new Intent(context, AlarmIntentService.class);
        intent.setAction(ACTION_RECALCULATE_ALARM);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RECALCULATE_ALARM.equals(action)) {
                handleActionResetAlarm();
            }
        }
    }

    private void handleActionResetAlarm() {
        // assuming that previous alarms are deleted...
        List<MyAlarm> list = MyAlarm.listAll(MyAlarm.class, "m_raw_alarm_time");
        if ((list != null) && (0 < list.size())) {
            MyAlarm first = list.get(0);
            Intent alertIntent = AlertReceiver.prepareForAlarm(this, first);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, first.getRawAlarmTime(), pendingIntent);

        }
    }
}
