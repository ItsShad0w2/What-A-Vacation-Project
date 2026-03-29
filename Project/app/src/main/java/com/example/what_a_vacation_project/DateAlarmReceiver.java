package com.example.what_a_vacation_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DateAlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Setting the alarms scheduled for the user in case of the phone being restarted and when the user is present

        String action = intent.getAction();

        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_USER_PRESENT.equals(intent.getAction()))
        {
            // Handling the schedule of the alarms while there is internet connection

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(AlarmsSync.class)
                    .setConstraints(constraints)
                    .build();

            WorkManager.getInstance(context).enqueueUniqueWork("SyncAlarms", ExistingWorkPolicy.KEEP, oneTimeWorkRequest);

        }
        else
        {
            // Handling the sending of notifications to the user when the alarm is triggered using an action that is set for the alarm

            if(action != null && action.equals("com.example.what_a_vacation_project.Alarm"))
            {
                String tripName = intent.getStringExtra("tripName");

                if (tripName != null) {
                    Notification.setNotification(context, tripName);
                }
            }
        }
    }

}
