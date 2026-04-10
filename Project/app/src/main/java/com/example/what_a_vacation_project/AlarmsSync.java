package com.example.what_a_vacation_project;

import android.content.Context;

import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jspecify.annotations.NonNull;


public class AlarmsSync extends ListenableWorker
{

    public AlarmsSync(@NonNull Context appContext, @NonNull WorkerParameters workerParams)
    {
        super(appContext, workerParams);
    }

    @Override
    public @NonNull ListenableFuture<Result> startWork()
    {
        // Starting the schedule of the alarms for the trips of the user when the application is started

        return CallbackToFutureAdapter.getFuture(completer -> {

            String userId = Firebase.firebaseAuth.getUid();

            if(userId == null)
            {
                completer.set(Result.failure());
                return "No user is logged in";
            }
            else
            {
                // Handling the schedule of the alarms from the database of the user's branch

                DatabaseReference referenceTrip = Firebase.getReferenceTrip(userId);

                referenceTrip.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                Trip trip = dataSnapshot.getValue(Trip.class);

                                if(trip != null)
                                {
                                    trip.setIdTrip(dataSnapshot.getKey());
                                    TripsLayout.setScheduleAlarm(getApplicationContext(), trip);
                                }
                            }
                        }

                        completer.set(Result.success());
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error)
                    {
                        // Attempting to do another schedule of the alarms in case of an error

                        completer.set(Result.retry());
                    }
                });

                return "FirebaseAlarmsSync";
            }
        });
    }
}
