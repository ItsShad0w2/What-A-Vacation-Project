package com.example.what_a_vacation_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Notification
{
    private static final String CHANNEL_ID = "channelID";
    private static final String CHANNEL_NAME = "channelName";
    private static final int notificationID = 1;

    public static void setNotification(Context context, String tripName)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Trip Reminder")
                .setContentText("Your " + tripName + " trip is coming up")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(notificationID, builder.build());

    }
}
