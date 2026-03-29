package com.example.what_a_vacation_project;

public interface CallBack
{
    // Interface for the response of acquiring the data of travel advice of countries from the server

    void onSuccess(String conditions);
    void onFailure(String error);
}
