package com.example.what_a_vacation_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DateAlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String tripName = intent.getStringExtra("tripName");
        Notification.setNotification(context, tripName);

    }
}
