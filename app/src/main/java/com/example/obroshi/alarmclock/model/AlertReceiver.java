package com.example.obroshi.alarmclock.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.obroshi.alarmclock.R;
import com.example.obroshi.alarmclock.view.MainActivity;

public class AlertReceiver extends BroadcastReceiver {

    private static final String EXTRA_ALARM_LABEL = "label";

    public static Intent prepareForAlarm(Context context, MyAlarm alarm){
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra(EXTRA_ALARM_LABEL, alarm.getAlarmLabel());
        return intent;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context,
                intent.getStringExtra(EXTRA_ALARM_LABEL),
                "5 seconds has passed",
                "Alert");

    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert){

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_alarm_add_white_24dp)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);

        mBuilder.setContentIntent(notificationIntent);

        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, mBuilder.build());
    }
}
