package com.example.what_a_vacation_project;

public class User
{
    private String userId;
    private String name;

    public User(String userId, String name)
    {
        this.userId = userId;
        this.name = name;
    }

    public User()
    {

    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }
}
