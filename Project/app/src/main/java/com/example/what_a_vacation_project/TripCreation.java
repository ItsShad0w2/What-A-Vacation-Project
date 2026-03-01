package com.example.what_a_vacation_project;

import static com.example.what_a_vacation_project.BuildConfig.GeminiAPIKey;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripCreation extends AppCompatActivity
{
    Spinner daysSpinner;
    GoogleMap googleMapFragment;
    Button previousScreen, homePage;
    private GeminiManager geminiManager;
    private String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_creation);

        tripId = getIntent().getStringExtra("tripId");
        boolean change = getIntent().getBooleanExtra("change", true);
        geminiManager = GeminiManager.getInstance();

        previousScreen = findViewById(R.id.previousScreen);
        homePage = findViewById(R.id.homePage);
        daysSpinner = findViewById(R.id.daysSpinner);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapFragment);

        Log.d("Country", Firebase.getReferenceTrip(Firebase.firebaseAuth.getUid()).child(tripId).child("Country").toString());

        if(supportMapFragment != null)
        {
            supportMapFragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap)
                {
                    googleMapFragment = googleMap;

                    if(change)
                    {
                        generateTrip();
                    }
                    else
                    {
                        loadTrip();
                    }
                }
            });
        }

        homePage.setOnClickListener(View -> {
            Intent intent = new Intent(this, tripsLayout.class);
            startActivity(intent);
        });

        previousScreen.setOnClickListener(View -> {
            Intent intent = new Intent(this, TripDetails.class);
            intent.putExtra("tripId", tripId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        Log.d("Trip creation", "Trip ID:" + tripId);

    }

    private void loadTrip()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Trip..");
        progressDialog.setMessage("This process may take a bit of time");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference referenceLocations = Firebase.getReferenceTrip(Firebase.firebaseAuth.getUid()).child(tripId).child("Locations");

        referenceLocations.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Map<String,List<Location>> locationsMap = new HashMap<>();

                for(DataSnapshot daySnapshot : snapshot.getChildren())
                {
                    String day = daySnapshot.getKey();
                    List<Location> locations = new ArrayList<>();

                    for(DataSnapshot locationSnapshot : daySnapshot.getChildren())
                    {
                        Location location = locationSnapshot.getValue(Location.class);

                        if(location != null)
                        {
                            locations.add(location);
                        }
                    }

                    locationsMap.put(day, locations);
                }

                progressDialog.dismiss();
                setDaysSpinner(locationsMap, daysSpinner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void generateTrip()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Generating Trip..");
        progressDialog.setMessage("This process may take a bit of time");
        progressDialog.setCancelable(false);
        progressDialog.show();
        DatabaseReference referenceTrip = Firebase.getReferenceTrip(Firebase.firebaseAuth.getUid());

        referenceTrip.child(tripId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!snapshot.exists()) {
                    progressDialog.dismiss();
                    Toast.makeText(TripCreation.this, "An error has occurred while generating the trip", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("Trip Creation", "Snapshot does exist");
                String country = snapshot.child("Country").getValue(String.class);
                String startDate = snapshot.child("StartDate").getValue(String.class);
                String endDate = snapshot.child("EndDate").getValue(String.class);
                String description = snapshot.child("Description").getValue(String.class);
                String fullPrompt = LocationsPrompts.locationsStructure(country, startDate, endDate, description);

                try
                {
                    geminiManager.generateTrip(fullPrompt, new GeminiCallBack()
                    {
                        @Override
                        public void onSuccess(String responseGiven)
                        {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Log.d("Response", responseGiven);
                                Map<String, List<Location>> locations = convertTrip(responseGiven);

                                if (locations != null && !locations.isEmpty())
                                {
                                    setDaysSpinner(locations, daysSpinner);
                                }
                                else
                                {
                                    Toast.makeText(TripCreation.this, "An error has occurred while generating the trip", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable exception)
                        {
                            super.onFailure(exception);
                            runOnUiThread(()-> {
                                if(progressDialog.isShowing() && progressDialog != null)
                                {
                                    progressDialog.dismiss();
                                    Log.d("Flow", "The code stops because of " + exception.getMessage());
                                }
                            });


                        }
                    });
                }
                catch(Exception exception)
                {
                    progressDialog.dismiss();
                    Toast.makeText(TripCreation.this, "An error has occurred while generating the trip", Toast.LENGTH_SHORT).show();
                    Log.d("Exception", exception.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                progressDialog.dismiss();
                Toast.makeText(TripCreation.this, "An error has occurred while generating the trip", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Map<String, List<Location>> convertTrip(String response)
    {
        if(tripId != null && response != null)
        {
            String trimmedResponse = response.replaceAll("```json|```", "").trim();

            try
            {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, List<Location>>>() {}.getType();
                Map<String, List<Location>> locations = gson.fromJson(trimmedResponse, type);

                DatabaseReference referenceTrip = Firebase.getReferenceTrip(Firebase.firebaseAuth.getUid());
                referenceTrip.child(tripId).child("Locations").setValue(locations);

                return locations;
            }
            catch (Exception exception)
            {
                Log.e("Exception", exception.getMessage());
                return null;
            }
        }

        return null;
    }

    public void setDaysSpinner(Map<String, List<Location>> locations, Spinner spinner)
    {
        List<String> days = new ArrayList<>(locations.keySet());

        Collections.sort(days, (item1, item2) -> {
            int num1 = Integer.parseInt(item1.replaceAll("[^0-9]", ""));
            int num2 = Integer.parseInt(item2.replaceAll("[^0-9]", ""));

            return Integer.compare(num1, num2);
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                String selectedDay = days.get(i);
                List<Location> selectedLocations = locations.get(selectedDay);

                if(googleMapFragment != null)
                {
                    googleMapFragment.clear();
                    markLocations(selectedLocations);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }

    public void markLocations(List<Location> locations)
    {
        if(locations == null)
        {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        PolylineOptions path = new PolylineOptions()
                .color(Color.YELLOW)
                .width(10)
                .geodesic(true);

        for(Location location: locations)
        {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMapFragment.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(location.getLocationName()));

            path.add(latLng);
            builder.include(latLng);
        }

        googleMapFragment.addPolyline(path);
        googleMapFragment.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));

    }


}
