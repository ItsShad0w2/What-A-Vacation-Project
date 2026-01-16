package com.example.what_a_vacation_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class tripsLayout extends AppCompatActivity
{

    RecyclerView tripsList;
    Button addTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_layout);

        tripsList = findViewById(R.id.tripsList);
        addTrip = findViewById(R.id.addTrip);

        setRecycler();
        addTrip.setOnClickListener(View ->{
            Intent intent = new Intent(this, TripDetails.class);
            startActivity(intent);
        });
    }

    public void setRecycler()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tripsList.setLayoutManager(linearLayoutManager);
    }
}