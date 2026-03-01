package com.example.what_a_vacation_project;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class tripsLayout extends AppCompatActivity
{

    RecyclerView recyclerView;
    TripAdapter adapter;
    Button addTrip;
    List<Trip> tripsList = new ArrayList<>();
    private long dayInMillis = 86400000;
    private long hourInMillis = 3600000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_layout);

        permissions();

        recyclerView = findViewById(R.id.tripsList);
        addTrip = findViewById(R.id.addTrip);

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
        loadTrips();

        addTrip.setOnClickListener(View -> {
            Intent intent = new Intent(this, TripDetails.class);
            startActivity(intent);
        });


    }

    public void permissions()
    {
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
                        setScheduleAlarm(trip);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Trip");
        builder.setMessage("Are you sure you want to delete this trip?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            Firebase.getReferenceTrip(Firebase.firebaseAuth.getUid()).child(trip.getIdTrip()).removeValue().addOnCompleteListener(aVoid -> {
                tripsList.remove(position);
                adapter.notifyItemRemoved(position);
                cancelScheduleAlarm(trip);
            });

        });

        builder.setNegativeButton("No", ((dialogInterface, i) -> {
            dialogInterface.dismiss();
        }));

        builder.show();
    }

    public void setScheduleAlarm(Trip trip)
    {
        long tripTime = convertDateToMillis(trip.getStartDate());
        long currentTime = System.currentTimeMillis();

        if ((tripTime - currentTime) >= dayInMillis)
        {
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, DateAlarmReceiver.class);
            intent.putExtra("tripName", trip.getName());
            int requestID = trip.getIdTrip().hashCode();

            long alarmTime = tripTime - dayInMillis;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null)
            {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
            }
        }
    }

    public void cancelScheduleAlarm(Trip trip)
    {
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

    public long convertDateToMillis(String date)
    {
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

