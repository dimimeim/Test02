package com.example.test.test01;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by dmeimaroglou on 30/6/2016.
 */
public class wakeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("---onReceive--- " + intent.toString());
        if (intent == null || intent.getAction() == null) return;
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            PendingIntent pendingIntent = PendingIntent.getBroadcast( context, 0, new Intent("dimimeim.intent.alarm"), 0 );
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int interval = 8000;
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
        }
        if (intent.getAction().equals("dimimeim.intent.alarm")) {

            Toast.makeText(context, "I'm IN", Toast.LENGTH_SHORT).show();
        }
        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }
}