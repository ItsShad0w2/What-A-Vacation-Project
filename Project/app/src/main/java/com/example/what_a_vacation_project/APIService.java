package com.example.what_a_vacation_project;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService
{
    // Interface for the connecting to the server to acquire the travel advice of countries
    @GET("destinations-export")
    Call<String> getConditions();

}
