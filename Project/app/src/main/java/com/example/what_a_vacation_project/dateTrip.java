package com.example.what_a_vacation_project;

import java.util.ArrayList;

public class dateTrip
{
    private String date;
    private ArrayList<Location> locationsPlanned;
    public dateTrip(String date, ArrayList<Location> locationsPlanned)
    {
        this.date = date;
        this.locationsPlanned = locationsPlanned;
    }

    public dateTrip()
    {

    }

    public String getDate()
    {
        return this.date;
    }

    public ArrayList<Location> getLocationsPlanned()
    {
        return locationsPlanned;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public void setLocationsPlanned(ArrayList<Location> locationsPlanned)
    {
        this.locationsPlanned = locationsPlanned;
    }
}
