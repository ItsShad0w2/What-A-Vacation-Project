package com.example.what_a_vacation_project;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService
{
    @GET("destinations-export")
    Call<String> getConditions();

}
