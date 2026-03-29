package com.example.what_a_vacation_project;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.Locale;

public class tripsLayout extends AppCompatActivity
{

    RecyclerView recyclerView;
    TripAdapter adapter;
    Button addTrip, logout;
    List<Trip> tripsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_layout);

        permissions();

        recyclerView = findViewById(R.id.tripsList);
        addTrip = findViewById(R.id.addTrip);
        logout = findViewById(R.id.logout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TripAdapter(tripsList, new TripAdapter.onTripClickListener()
        {
            @Override
            public void onTripClick(Trip trip)
            {
                Intent intent = new Intent(tripsLayout.this, TripDetails.class);
                intent.putExtra("tripId", trip.getIdTrip());
                startActivity(intent);
            }

            @Override
            public void onTripLongClick(Trip trip, int position)
            {
                deleteTrip(trip, position);
            }
        });

        recyclerView.setAdapter(adapter);

        addTrip.setOnClickListener(View -> {
            Intent intent = new Intent(this, TripDetails.class);
            startActivity(intent);
        });

        logout.setOnClickListener(View -> {
            logOut();
        });
    }

    @Override
    protected void onStart()
    {
        // Ensuring that when the screen is started, the trips are loaded with the latest data saved from the database

        super.onStart();
        loadTrips();
    }

    public void permissions()
    {
        // Permission being requested to allow notifications to be sent

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
        {
            if(checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED)
            {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    public void loadTrips()
    {
        // Loading the trips from the database and setting them in the recycler view including scheduling their alarms

        Firebase.getReferenceTrip(Firebase.firebaseAuth.getUid()).orderByChild("StartDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                tripsList.clear();

                for (DataSnapshot tripSnapshot : snapshot.getChildren())
                {
                    Trip trip = tripSnapshot.getValue(Trip.class);

                    if (trip != null)
                    {
                        trip.setIdTrip(tripSnapshot.getKey());
                        tripsList.add(trip);
                        setScheduleAlarm(tripsLayout.this, trip);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    public void deleteTrip(Trip trip, int position)
    {
        // Alert dialog to confirm the deletion of a trip and rearrange the recycler view

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Trip");
        builder.setMessage("Are you sure you want to delete this trip?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            Firebase.getReferenceTrip(Firebase.firebaseAuth.getUid()).child(trip.getIdTrip()).removeValue().addOnCompleteListener(aVoid -> {
                tripsList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, tripsList.size());

                //Cancel the alarm in case it does exist
                cancelScheduleAlarm(trip);
            });

        });

        builder.setNegativeButton("No", ((dialogInterface, i) -> {
            dialogInterface.dismiss();
        }));

        builder.show();
    }

    public void logOut()
    {
        // Cancel the alarms of set trips and log out the user

        for(Trip trip : tripsList)
        {
            cancelScheduleAlarm(trip);
        }

        Firebase.firebaseAuth.signOut();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static void setScheduleAlarm(Context context, Trip trip)
    {
        // Cases of the trip not existing, cancelling the alarm

        if(trip == null)
        {
            return;
        }

        if(trip.getIdTrip() == null)
        {
            Log.d("AlarmManager", "There is no ID to the trip " + trip.getName());
            return;
        }

        // Set the alarm to a day prior to the trip's start date
        // That is in case that the trip wasn't set on the day it occurs

        long tripTime = convertDateToMillis(trip.getStartDate());
        long currentTime = System.currentTimeMillis();

        long dayInMillis = 86400000;

        if((tripTime - currentTime) >= dayInMillis)
        {
            long alarmTime = tripTime - dayInMillis;

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, DateAlarmReceiver.class);
            intent.putExtra("tripName", trip.getName());
            intent.setAction("com.example.what_a_vacation_project.Alarm");
            int requestID = trip.getIdTrip().hashCode();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null)
            {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
                Log.d("AlarmManager", "Alarm was set");
            }
        }
    }

    public void cancelScheduleAlarm(Trip trip)
    {
        // Cancel the alarm of a trip using the ID of the trip related to it

        Intent intent = new Intent(this, DateAlarmReceiver.class);
        int requestID = trip.getIdTrip().hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestID, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null)
        {
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static long convertDateToMillis(String date)
    {
        // Set the time of the alarm being sent to a time around 8:00 AM

        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(date));

            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            return calendar.getTimeInMillis();
        }

        catch (Exception exception)
        {
            Log.d("Exception", exception.getMessage());
        }

        return 0;
    }
}
