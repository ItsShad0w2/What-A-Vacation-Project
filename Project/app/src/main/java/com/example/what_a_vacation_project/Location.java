package com.example.what_a_vacation_project;

import com.google.gson.annotations.SerializedName;

public class Location
{
    @SerializedName("name")
    private String locationName;
    private String description;
    private String placeId;
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;

    public Location(String locationName, String description, String placeId, double latitude, double longitude)
    {
        this.locationName = locationName;
        this.description = description;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location()
    {

    }

    public String getLocationName()
    {
        return locationName;
    }
    public String getDescription()
    {
        return description;
    }

    public String getPlaceId()
    {
        return placeId;
    }
    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setPlaceId(String placeId)
    {
        this.placeId = placeId;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }
}
