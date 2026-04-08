package com.example.what_a_vacation_project;

import com.google.firebase.database.PropertyName;

import java.util.List;
import java.util.Map;

public class Trip
{
    @PropertyName("Name")
    private String name;
    @PropertyName("Country")
    private String country;
    private String idTrip;
    private Map<String, List<Location>> locations;
    @PropertyName("Description")
    private String description;
    @PropertyName("StartDate")
    private String startDate;
    @PropertyName("EndDate")
    private String endDate;

    public Trip(String name, String country, String idTrip, String description, Map<String, List<Location>> locations, String startDate, String endDate)
    {
        this.name = name;
        this.country = country;
        this.idTrip = idTrip;
        this.description = description;
        this.locations = locations;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Trip()
    {

    }

    @PropertyName("Name")
    public String getName()
    {
        return name;
    }
    @PropertyName("Country")
    public String getCountry()
    {
        return country;
    }

    public String getIdTrip()
    {
        return idTrip;
    }

    @PropertyName("Description")
    public String getDescription()
    {
        return description;
    }

    public Map<String, List<Location>> getLocations()
    {
        return locations;
    }

    @PropertyName("StartDate")
    public String getStartDate()
    {
        return startDate;
    }

    @PropertyName("EndDate")
    public String getEndDate()
    {
        return endDate;
    }

    @PropertyName("Name")
    public void setName(String name)
    {
        this.name = name;
    }
    @PropertyName("Country")
    public void setCountry(String country)
    {
        this.country = country;
    }

    public void setIdTrip(String idTrip)
    {
        this.idTrip = idTrip;
    }

    @PropertyName("Description")
    public void setDescription(String description)
    {
        this.description = description;
    }
    public void setLocations(Map<String, List<Location>> locations)
    {
        this.locations = locations;
    }
    @PropertyName("StartDate")
    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    @PropertyName("EndDate")
    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }


}
