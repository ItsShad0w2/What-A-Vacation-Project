package com.example.what_a_vacation_project;

import com.google.gson.annotations.SerializedName;

public class Location
{
    @SerializedName("name")
    private String locationName;
    private String startDate;
    private String description;
    private String endDate;
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;

    public Location(String locationName, String description, String startDate, String endDate, double latitude, double longitude)
    {
        this.locationName = locationName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(String locationName, double latitude, double longitude)
    {
        this.locationName = locationName;
        this.description = description;
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

    public String getStartDate()
    {
        return startDate;
    }

    public String getEndDate()
    {
        return endDate;
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

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
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
