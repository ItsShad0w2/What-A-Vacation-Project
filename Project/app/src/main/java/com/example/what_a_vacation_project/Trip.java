package com.example.what_a_vacation_project;

import java.util.ArrayList;

public class Trip
{
    private String country;
    private String idTrip;
    private ArrayList<dateTrip> datesPlanned;
    private String startDate;
    private String endDate;

    public Trip(String country, String idTrip, ArrayList<dateTrip> datesPlanned, String startDate, String endDate)
    {
        this.country = country;
        this.idTrip = idTrip;
        this.datesPlanned = datesPlanned;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Trip()
    {

    }

    public String getCountry()
    {
        return country;
    }

    public String idTrip()
    {
        return idTrip;
    }

    public ArrayList<dateTrip> getDatesPlanned()
    {
        return datesPlanned;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public void setIdTrip(String idTrip)
    {
        this.idTrip = idTrip;
    }

    public void setDatesPlanned(ArrayList<dateTrip> datesPlanned)
    {
        this.datesPlanned = datesPlanned;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }


}
