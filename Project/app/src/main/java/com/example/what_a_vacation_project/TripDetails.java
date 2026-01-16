package com.example.what_a_vacation_project;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class TripDetails extends AppCompatActivity
{
    AutoCompleteTextView countries;
    EditText tripName;
    ImageButton calendarButton;
    Button setTripButton;
    private String startDate = "";
    private String endDate = "";
    private List<String> listedCountries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

         countries = findViewById(R.id.countries);
         tripName = findViewById(R.id.tripName);
         calendarButton = findViewById(R.id.calendarButton);
         setTripButton = findViewById(R.id.setTripButton);

        setCalendar();

        setTripButton.setOnClickListener(View -> {
            setTrip();
        });

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getCountries());
        countries.setAdapter(adapter);

    }

    public void setTrip()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM)
        {
            if(tripName.getText().isEmpty() || startDate.isEmpty() || endDate.isEmpty() || countries.getText().isEmpty())
            {
                Toast.makeText(this, "Please fill in all of the fields", Toast.LENGTH_SHORT).show();
            }
            else
            {
                boolean countryFound = false;
                for(String country : listedCountries)
                {
                    if(country.equals(countries.getText().toString()))
                    {
                        countryFound = true;
                        break;
                    }
                }

                if(!countryFound)
                {
                    countries.setText("");
                    Toast.makeText(this, "Invalid Country", Toast.LENGTH_SHORT).show();
                }
                {
                    Intent intent = new Intent(this, TripCreation.class);
                    intent.putExtra("tripName", tripName.getText().toString());
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("endDate", endDate);
                    intent.putExtra("country", countries.getText().toString());
                    startActivity(intent);
                }
            }
        }


    }

    public void setCalendar()
    {
        long current = MaterialDatePicker.todayInUtcMilliseconds();
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(current))
                .build();

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select the trip's range of dates");
        builder.setCalendarConstraints(constraints);

        MaterialDatePicker<Pair<Long, Long>> MaterialDatePicker = builder.build();
        calendarButton.setOnClickListener(View -> {
            MaterialDatePicker.show(getSupportFragmentManager(), "PICK DATE");

            MaterialDatePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                startDate = simpleDateFormat.format(new Date(selection.first));
                endDate = simpleDateFormat.format(new Date(selection.second));

            });
        });
    }

    public String[] getCountries()
    {
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

}