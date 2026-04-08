package com.example.what_a_vacation_project;

import static com.example.what_a_vacation_project.Firebase.getReferenceTrip;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class TripDetails extends AppCompatActivity
{
    AutoCompleteTextView countries;
    EditText tripName, generateTripDetails;
    TextView conditionView;
    ImageView levelView;
    ImageButton calendarButton;
    Button generateTripButton, tripLayoutButton;
    private String startDate = "";
    private String endDate = "";
    private List<String> listedCountries = new ArrayList<>();
    private String originalName = "";
    private String originalCountry = "";
    private String originalStartDate = "";
    private String originalEndDate = "";
    private String originalDescription = "";
    private String currentTripId = null;

    ConditionAPI conditionAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        countries = findViewById(R.id.countries);
        tripName = findViewById(R.id.tripName);
        generateTripDetails = findViewById(R.id.generateTripDetails);
        calendarButton = findViewById(R.id.calendarButton);
        generateTripButton = findViewById(R.id.generateTripButton);
        tripLayoutButton = findViewById(R.id.tripLayoutButton);
        conditionView = findViewById(R.id.conditionView);
        levelView = findViewById(R.id.levelView);
        conditionAPI = new ConditionAPI();

        if (getIntent().hasExtra("tripId"))
        {
            currentTripId = getIntent().getStringExtra("tripId");
        }

        loadExistingTrip();

        generateTripButton.setOnClickListener(View -> {
            setTrip();
        });

        tripLayoutButton.setOnClickListener(View -> {
            Intent intent = new Intent(this, tripsLayout.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getCountries());
        countries.setAdapter(adapter);

        countries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                countryAdvice();
            }
        });
    }

    public void loadExistingTrip()
    {
        // Load the trip's data in case that it does exist

        if(currentTripId != null)
        {
            getReferenceTrip(Firebase.firebaseAuth.getUid()).child(currentTripId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        // Acquiring the data of the trip and the travel advice of the trip's country, setting it in the fields of the trip

                        originalName = snapshot.child("Name").getValue(String.class);
                        originalCountry = snapshot.child("Country").getValue(String.class);
                        originalDescription = snapshot.child("Description").getValue(String.class);
                        originalStartDate = snapshot.child("StartDate").getValue(String.class);
                        originalEndDate = snapshot.child("EndDate").getValue(String.class);

                        startDate = originalStartDate;
                        endDate = originalEndDate;

                        tripName.setText(originalName);
                        countries.setText(originalCountry);
                        generateTripDetails.setText(originalDescription);

                        if(!countries.getText().toString().isEmpty() && countries != null)
                        {
                            countryAdvice();
                        }

                        setCalendar(originalStartDate, originalEndDate);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {
                    Toast.makeText(com.example.what_a_vacation_project.TripDetails.this, "An error has occurred while loading the trip", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            setCalendar("", "");
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        // Ensuring that when entering the screen from different activities, the trip's data is loaded in case a trip was created

        super.onNewIntent(intent);

        setIntent(intent);

        if(getIntent().hasExtra("tripId"))
        {
            currentTripId = getIntent().getStringExtra("tripId");
            loadExistingTrip();
        }
    }

    public void countryAdvice()
    {
            // Calling the API to get the travel advice regarding the trip's country

        String country = countries.getText().toString();
        try
        {
            conditionAPI.getConditions(adjustments(country), com.example.what_a_vacation_project.TripDetails.this, new CallBack()
            {
                @Override
                public void onSuccess(String conditions)
                {
                    setCondition(conditions);
                }

                @Override
                public void onFailure(String error)
                {
                    Toast.makeText(com.example.what_a_vacation_project.TripDetails.this, "An error has occurred.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception exception)
        {
            Toast.makeText(this, "An error has occurred.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasChanged()
    {
        // A check in case that the details of the trip have changed and generating a new one accordingly

        if(currentTripId == null)
        {
            return true;
        }

        String currentCountry = countries.getText().toString().trim();
        String currentDescription = generateTripDetails.getText().toString().trim();

        return  !currentCountry.equals(originalCountry.trim()) ||
                !currentDescription.equals(originalDescription.trim()) ||
                !startDate.equals(originalStartDate) ||
                !endDate.equals(originalEndDate);
    }
    public void setTrip()
    {
        // Acquiring the data of the trip and saving it inside of the database

        if (tripName.getText().toString().isEmpty() || startDate.isEmpty() || endDate.isEmpty() || countries.getText().toString().isEmpty() || generateTripDetails.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please fill in all of the required fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Checking that the country set is valid

            boolean countryFound = false;
            for (String country : listedCountries)
            {
                if (country.equals(countries.getText().toString()))
                {
                    countryFound = true;
                    break;
                }
            }

            if (!countryFound)
            {
                countries.setText("");
                Toast.makeText(this, "Invalid Country", Toast.LENGTH_SHORT).show();
            }
            else
            {
                // The save of the trip's data inside of the database
                // Ensuring that in case the details of the trip have changed from its prior, there would be a generation of a new one

                boolean changed = hasChanged();
                String userId = Firebase.firebaseAuth.getUid();

                if (userId != null)
                {
                    DatabaseReference referenceUser = getReferenceTrip(userId);

                    if (currentTripId == null)
                    {
                        currentTripId = referenceUser.push().getKey();
                    }

                    if (currentTripId != null)
                    {
                        Map<String, Object> tripData = new HashMap<>();

                        tripData.put("Name", tripName.getText().toString());
                        tripData.put("StartDate", startDate);
                        tripData.put("EndDate", endDate);
                        tripData.put("Country", countries.getText().toString());
                        tripData.put("Description", generateTripDetails.getText().toString());

                        referenceUser.child(currentTripId).updateChildren(tripData).addOnCompleteListener(task ->
                        {
                            if (task.isSuccessful())
                            {
                                originalName = tripName.getText().toString();
                                originalCountry = countries.getText().toString();
                                originalDescription = generateTripDetails.getText().toString();
                                originalStartDate = startDate;
                                originalEndDate = endDate;

                                Intent intent = new Intent(this, TripCreation.class);
                                intent.putExtra("tripId", currentTripId);
                                intent.putExtra("change", changed);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        }
    }

    public void setCalendar(String originalStartDate, String originalEndDate)
    {
        // Setting the calendar for the trip's dates including constraints for the selection of dates

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        long current = MaterialDatePicker.todayInUtcMilliseconds();
        CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select the trip's range of dates");

        if(originalStartDate == null || originalEndDate == null || originalStartDate.isEmpty() || originalEndDate.isEmpty())
        {
            constraints.setValidator(DateValidatorPointForward.from(current));
        }
        else
        {
            try
            {
                Date startDate = simpleDateFormat.parse(originalStartDate);
                Date endDate = simpleDateFormat.parse(originalEndDate);

                if(startDate != null && endDate != null)
                {
                    if (startDate.getTime() < current)
                    {
                        constraints.setValidator(DateValidatorPointForward.from(startDate.getTime()));
                    }
                    else
                    {
                        constraints.setValidator(DateValidatorPointForward.from(current));
                    }

                    builder.setSelection(Pair.create(startDate.getTime(), endDate.getTime()));
                    constraints.setOpenAt(startDate.getTime());
                }

            }
            catch (Exception exception)
            {
                constraints.setValidator(DateValidatorPointForward.from(current));
            }
        }

        builder.setCalendarConstraints(constraints.build());

        MaterialDatePicker<Pair<Long, Long>> MaterialDatePicker = builder.build();
        calendarButton.setOnClickListener(View -> {
            MaterialDatePicker.show(getSupportFragmentManager(), "PICK DATE");

            MaterialDatePicker.addOnPositiveButtonClickListener(selection -> {
                startDate = simpleDateFormat.format(new Date(selection.first));
                endDate = simpleDateFormat.format(new Date(selection.second));

            });
        });
    }

    public String[] getCountries()
    {
        // Acquiring from the raw folder the countries available for the user to set the trip's country to

        InputStream inputStream = getResources().openRawResource(R.raw.countries);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try
        {
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                listedCountries.add(line);
            }

            bufferedReader.close();
            inputStream.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return listedCountries.toArray(new String[0]);
    }


    public void setCondition(String conditions)
    {
        // Setting the travel advice for the trip's country and showing it accordingly

        levelView.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        Condition condition;

        if(conditions != null && !conditions.isEmpty())
        {
            condition = gson.fromJson(conditions, Condition.class);

            if(condition.getField_overall_advice_level().equals("Exercise a high degree of caution"))
            {
                levelView.setImageResource(R.drawable.level2);
            }
            else
            {
                if(condition.getField_overall_advice_level().equals("Reconsider your need to travel"))
                {
                    levelView.setImageResource(R.drawable.level3);
                }

                if(condition.getField_overall_advice_level().equals("Do not travel"))
                {
                    levelView.setImageResource(R.drawable.level4);
                }

                if(condition.getField_overall_advice_level().equals("Exercise normal safety precautions"))
                {
                    levelView.setImageResource(R.drawable.level1);
                }
            }

            conditionView.setText(condition.toString());
        }
        else
        {
            if(countries.getText().toString().equalsIgnoreCase("Australia"))
            {
                // Due to the API being of the Australian officials' advice, a one was set to Australia itself as the API doesn't contain data on it

                levelView.setImageResource(R.drawable.level1);
                condition = new Condition(
                        "Australia",
                        "Exercise normal safety precautions",
                        "As last checked and reviewed, travelling in Australia is safe for all travellers. You may want to be alert for any kinds of protests that are occurring in the country due to potential unrest and violence if not taking caution. Take note that this is not from an official advisory and might not be up to date. You may refer to the URL below for the current and official advice.",
                        date(),
                        "https://travel.gc.ca/destinations/australia");

                conditionView.setText(condition.toString());
            }
            else
            {
                conditionView.setText("No conditions found for this country or it doesn't exist. Make sure you're typing the country's name correctly.");
                levelView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public String date()
    {
        // Setting the current date of the travel advice to Australia to be the current date

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return formatDate.format(new Date(currentTime));
    }

    public String adjustments(String country)
    {
        // Due to the API storing specific names that don't match how most users would be likely to spell their names, there is an adjustment to the country's name

        if(country.equals("North Korea"))
        {
            country = "North Korea (Democratic People&#039;s Republic of Korea)";
        }

        if(country.equals("South Korea"))
        {
            country = "South Korea (Republic of Korea)";
        }

        if(country.equals("United States"))
        {
            country = "United States of America";
        }

        return country;
    }
}