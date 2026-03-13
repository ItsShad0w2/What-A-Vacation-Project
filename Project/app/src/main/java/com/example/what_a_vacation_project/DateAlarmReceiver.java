package com.example.what_a_vacation_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DateAlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_USER_PRESENT.equals(intent.getAction()))
        {
            setTripsAlarms(context);
        }
        else
        {
            String tripName = intent.getStringExtra("tripName");
            if (tripName != null)
            {
                Notification.setNotification(context, tripName);
            }
        }

    }

    public void setTripsAlarms(Context context)
    {
        String userId = Firebase.firebaseAuth.getUid();

        if(userId != null)
        {
            DatabaseReference referenceTrip = Firebase.getReferenceTrip(userId);

            referenceTrip.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            Trip trip = dataSnapshot.getValue(Trip.class);

                            if(trip != null)
                            {
                                trip.setIdTrip(dataSnapshot.getKey());
                                tripsLayout.setScheduleAlarm(context, trip);
                            }
                        }
                    }
                    else
                    {
                        Log.d("AlarmManager", "No trips found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {
                    Log.d("AlarmManager", "There was an error while getting the trips which is " + error.getMessage());
                }
            });
        }
    }
}
