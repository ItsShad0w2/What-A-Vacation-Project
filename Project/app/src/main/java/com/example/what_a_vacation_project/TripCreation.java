package com.example.what_a_vacation_project;

import android.app.AlertDialog;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TripCreation extends AppCompatActivity
{
    Spinner daysSpinner;
    GoogleMap googleMapFragment;
    View tripView, errorLayout;
    Button previousScreen, homePage, retryButton, deleteTrip;
    private GeminiManager geminiManager;
    private PlacesClient placesClient;
    private String tripId;
    private boolean change;
    private long dayInMillis = 86400000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_creation);

        geminiManager = GeminiManager.getInstance();

        previousScreen = findViewById(R.id.previousScreen);
        homePage = findViewById(R.id.homePage);
        retryButton = findViewById(R.id.retryButton);
        deleteTrip = findViewById(R.id.deleteTrip);
        daysSpinner = findViewById(R.id.daysSpinner);

        errorLayout = findViewById(R.id.errorLayout);
        tripView = findViewById(R.id.tripView);

        if(savedInstanceState != null)
        {
            // The activity was stopped
            // Retrieving the trip's ID, whether it is a new trip and whether there should be generation of a new trip

            tripId = savedInstanceState.getString("tripId");
            change = savedInstanceState.getBoolean("change", false);
        }
        else
        {
            // The activity wasn't stopped
            // The trip's ID, whether it is a new trip and whether there should be generation of a new trip are retrieved from the TripDetails activity

            tripId = getIntent().getStringExtra("tripId");
            change = getIntent().getBooleanExtra("change", false);
        }

        if(tripId == null)
        {
            // Returning to the TripDetails activity in case the trip's ID is null

            Log.d("TripId", "The trip ID is null");
            returnToTripDetails();
            return;
        }

        Log.d("TripId", "Intent TripID: " + tripId);
        Log.d("TripId", "SavedInstanceID: " + (savedInstanceState != null ? savedInstanceState.getString("tripId") : "null"));


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapFragment);

        if(!Places.isInitialized())
        {
            Places.initialize(getApplicationContext(), BuildConfig.GooglePlacesAPIKey);
        }

        placesClient = Places.createClient(this);

        if(supportMapFragment != null)
        {
            supportMapFragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap)
                {
                    googleMapFragment = googleMap;

                    googleMapFragment.setInfoWindowAdapter(new InformationWindowAdapter(TripCreation.this, placesClient));

                    // In case the data of the trip has changed, there would be a generation of a new one
                    // Otherwise, the trip's data would be loaded as the trip was made


                    Log.d("Change", "Change is " + change);

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
        // Loading the trip's data from the database and setting it to the spinner

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
                if(!snapshot.exists() || snapshot.getChildrenCount() == 0)
                {
                    progressDialog.dismiss();
                    generateTrip();
                    return;
                }

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
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
    private void generateTrip()
    {
        // Generating the trip using the acquired trip's data using the Gemini API

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
                if (!snapshot.exists())
                {
                    progressDialog.dismiss();
                    Toast.makeText(TripCreation.this, "An error has occurred while generating the trip", Toast.LENGTH_SHORT).show();
                    returnToTripDetails();
                }

                Log.d("Trip Creation", "Snapshot does exist");
                String country = snapshot.child("Country").getValue(String.class);
                String startDate = snapshot.child("StartDate").getValue(String.class);
                String endDate = snapshot.child("EndDate").getValue(String.class);
                String description = snapshot.child("Description").getValue(String.class);
                String fullPrompt = LocationsPrompts.locationsStructure(country, startDate, endDate, description, daysDifference(startDate, endDate));


                Log.d("Trip Creation", "The country is " + country + " and the prompt is " + fullPrompt);

                try
                {
                    // Calling the GeminiAPI to acquire the locations for the trip

                    geminiManager.generateTrip(fullPrompt, new GeminiCallBack()
                    {
                        @Override
                        public void onSuccess(String responseGiven)
                        {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Map<String, List<Location>> locations = convertTrip(responseGiven);

                                Log.d("Trip Creation", "Gemini response is " + responseGiven);

                                if (locations != null)
                                {
                                    if (!locations.isEmpty())
                                    {
                                        setDaysSpinner(locations, daysSpinner);
                                    }
                                    else
                                    {
                                        // Returning to the previous screen due to the preferences not matching what is related to a trip

                                        progressDialog.dismiss();
                                        Toast.makeText(TripCreation.this, "The preference for the trip don't seem to be related to a one, make sure to change it accordingly.", Toast.LENGTH_LONG).show();
                                        returnToTripDetails();
                                    }
                                }
                                else
                                {
                                    Log.e("Exception", "The error for the response trip is for not having locations given");
                                    handlingTripNotGenerating(progressDialog);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable exception)
                        {
                            super.onFailure(exception);
                            Log.e("Exception", "The error for the trip generation is " + exception.getMessage());
                            handlingTripNotGenerating(progressDialog);

                        }
                    });
                }
                catch(Exception exception)
                {
                    Log.e("Exception", "The error for the trip generation is " + exception.getMessage());
                    handlingTripNotGenerating(progressDialog);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Log.e("Exception", "The error for the trip generation is " + error.getMessage());
                handlingTripNotGenerating(progressDialog);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        // Saving the trip's ID, whether it is a new trip and whether there should be generation of a new trip when the activity is stopped

        super.onSaveInstanceState(outState);
        outState.putString("tripId", tripId);
        outState.putBoolean("change", change);
    }

    public long daysDifference(String startDate, String endDate)
    {
        // Calculating the number of days between the starting date and the end date of the trip in order to generate the trip for each day it occurs

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        long difference = 0;

        try
        {
            Date start = simpleDateFormat.parse(startDate);
            Date end = simpleDateFormat.parse(endDate);

            difference = ((Math.abs(end.getTime() - start.getTime())) / dayInMillis) + 1;
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }

        return difference;
    }

    public Map<String, List<Location>> convertTrip(String response)
    {
        // Converting the message received from the Gemini API to a map of days and their locations and saving it inside of the database

        if(tripId != null && response != null)
        {
            String trimmedResponse = response.replaceAll("```json|```", "").trim();

            Log.d("Trip Creation", "The response is " + trimmedResponse);

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
        // Setting the days of the trip to the spinner in an ascending order

        List<String> days = new ArrayList<>(locations.keySet());

        Collections.sort(days, (item1, item2) -> {
            int num1 = Integer.parseInt(item1.replaceAll("[^0-9]", ""));
            int num2 = Integer.parseInt(item2.replaceAll("[^0-9]", ""));

            return Integer.compare(num1, num2);
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Showing the locations of the trip according to each day of the trip on a map

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                String selectedDay = days.get(i);
                List<Location> selectedLocations = locations.get(selectedDay);

                if(googleMapFragment != null && selectedLocations != null)
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

        if(locations == null || locations.isEmpty())
        {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        PolylineOptions path = new PolylineOptions()
                .color(Color.YELLOW)
                .width(10)
                .geodesic(true);

        // Showing the details of the locations on the map when clicked on and setting the path of the trip

        InformationWindowAdapter informationWindowAdapter = new InformationWindowAdapter(this, placesClient);
        googleMapFragment.setInfoWindowAdapter(informationWindowAdapter);

        for(Location location: locations)
        {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(location.getLocationName())
                    .snippet(location.getDescription());

            Marker marker = googleMapFragment.addMarker(markerOptions);

            if(marker != null)
            {
                informationWindowAdapter.getPlaceImage(location.getLocationName(), marker);
            }

            path.add(latLng);
            builder.include(latLng);
        }

        googleMapFragment.addPolyline(path);

        // Setting the camera to the locations of the trip
        // In case the locations are null, the camera would be set to the first location of the trip

        try
        {
            googleMapFragment.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
        }
        catch(IllegalStateException exception)
        {
            googleMapFragment.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude())));
        }
    }

    @Override
    protected void onDestroy()
    {
        // Clearing the places client when the activity is being destroyed

        super.onDestroy();

        placesClient = null;
    }

    public void handlingTripNotGenerating(ProgressDialog progressDialog)
    {
        // Handling the occurrence of the trip not being generated

        runOnUiThread(() -> {
           if(progressDialog.isShowing())
           {
               progressDialog.dismiss();
           }

           tripView.setVisibility(View.GONE);
           errorLayout.setVisibility(View.VISIBLE);
           Toast.makeText(TripCreation.this, "An error has occurred while generating the trip", Toast.LENGTH_SHORT).show();

           retryButton.setOnClickListener(View -> {
               tripView.setVisibility(View.VISIBLE);
               errorLayout.setVisibility(View.GONE);
               generateTrip();
           });

           // Handling of deletion of the trip in case the user would decide to delete it

           deleteTrip.setOnClickListener(View -> {
               AlertDialog.Builder builder = new AlertDialog.Builder(TripCreation.this);
               builder.setTitle("Delete current trip");
               builder.setMessage("Are you sure you want to delete the current trip?");
               builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                   DatabaseReference referenceTrip = Firebase.getReferenceTrip(Firebase.firebaseAuth.getUid());
                   referenceTrip.child(tripId).removeValue();

                   Intent intent = new Intent(TripCreation.this, tripsLayout.class);
                   startActivity(intent);
               });

               builder.setNegativeButton("No", ((dialogInterface, i) -> {
                   dialogInterface.dismiss();
               }));

               builder.show();
           });
        });
    }

    public void returnToTripDetails()
    {
        // A method for ensuring the user would be sent to the TripDetails screen including clearing the activity's stack

        Intent intent = new Intent (TripCreation.this, TripDetails.class);
        intent.putExtra("tripId", tripId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}
