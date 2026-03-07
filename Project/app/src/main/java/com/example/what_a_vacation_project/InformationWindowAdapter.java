package com.example.what_a_vacation_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InformationWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    private final View view;
    private final PlacesClient placeClient;
    private final Map<String, Bitmap> imagesStored = new HashMap<>();

    public InformationWindowAdapter(Context context, PlacesClient placesClient)
    {
        view = LayoutInflater.from(context).inflate(R.layout.information_window, null);
        this.placeClient = placesClient;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker)
    {
        return null;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker)
    {
        TextView placeName = view.findViewById(R.id.placeName);
        ImageView placeImage = view.findViewById(R.id.placeImage);

        String locationName = marker.getTitle();
        placeName.setText(marker.getTitle());

        if(imagesStored.containsKey(locationName))
        {
            placeImage.setImageBitmap(imagesStored.get(locationName));
            placeImage.setVisibility(View.VISIBLE);
        }
        else
        {
            placeImage.setVisibility(View.GONE);
            getPlaceImage(locationName, marker);
        }

        return view;
    }

    public void getPlaceImage(String placeName, Marker marker)
    {
        if(imagesStored.containsKey(placeName))
        {
            return;
        }

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(placeName)
                .build();

        placeClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            if (!response.getAutocompletePredictions().isEmpty())
            {
                String placeId = response.getAutocompletePredictions().get(0).getPlaceId();

                findPlaceImage(placeId, placeName, marker);
            }
        }).addOnFailureListener(exception -> {
            Log.d("ExceptionSearching", String.valueOf(exception));
        });
    }

    public void findPlaceImage(String placeId, String locationName, Marker marker)
    {
        List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, fields);

        placeClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(response -> {
                Place place = response.getPlace();

                if(place.getPhotoMetadatas() != null && !place.getPhotoMetadatas().isEmpty())
                {
                    PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                    FetchPhotoRequest imageRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxWidth(500)
                            .build();

                    placeClient.fetchPhoto(imageRequest).addOnSuccessListener(fetchPhotoResponse -> {

                        imagesStored.put(locationName, fetchPhotoResponse.getBitmap());

                        if(marker.isInfoWindowShown())
                        {
                            marker.showInfoWindow();
                        }

                    }).addOnFailureListener(exception -> {
                        Log.d("ExceptionWithImage", String.valueOf(exception));
                    });
                }
            }
        ).addOnFailureListener(exception -> {
            Log.d("ExceptionWithAPI", String.valueOf(exception));
        });
    }
}
