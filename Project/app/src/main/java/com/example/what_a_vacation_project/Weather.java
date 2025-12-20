package com.example.what_a_vacation_project;

import java.util.ArrayList;

public class Weather
{
    private double longitude;
    private double latitude;
    private String timezone;
    private ArrayList<Trip> dates;
    private double[] minimumTemperature;
    private double[] maximumTemperature;
    private String weatherCode;

    public Weather(double longitude, double latitude, String timezone, ArrayList<Trip> dates, double[] minimumTemperature, double[] maximumTemperature, String weatherCode)
    {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timezone = timezone;
        this.dates = dates;
        this.minimumTemperature = minimumTemperature;
        this.maximumTemperature = maximumTemperature;
        this.weatherCode = weatherCode;
    }

    public double averageTemperature(double[] minimumTemperature, double[] maximumTemperature)
    {
        double sum = 0;
        for (int i = 0; i < minimumTemperature.length; i++)
        {
            sum += maximumTemperature[i] + minimumTemperature[i];
        }

        return sum;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public String getTimezone()
    {
        return timezone;
    }

    public ArrayList<Trip> getDates()
    {
        return dates;
    }

    public double[] getMinimumTemperature()
    {
        return minimumTemperature;
    }

    public double[] getMaxmimumTemperature()
    {
        return maximumTemperature;
    }

    public String getWeatherCode()
    {
        return weatherCode;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public void setTimezone(String timezone)
    {
        this.timezone = timezone;
    }

    public void setDates(ArrayList<Trip> dates)
    {
        this.dates = dates;
    }

    public void setMinimumTemperature(double[] minimumTemperature)
    {
        this.minimumTemperature = minimumTemperature;
    }

    public void setMaximumTemperature(double[] maximumTemperature)
    {
        this.maximumTemperature = maximumTemperature;
    }

    public void setWeatherCode(String weatherCode)
    {
        this.weatherCode = weatherCode;
    }
}
