package com.example.what_a_vacation_project;

import com.google.ai.client.generativeai.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;

public class GeminiManager
{
    private GeminiManager instance;
    private final GenerativeModel gemini;

    public GeminiManager(GenerativeModel gemini)
    {
        gemini = new GenerativeModel("gemini-2.5-flash", BuildConfig.GeminiAPIKey);
    }

    public GenerativeModel getInstance()
    {
        if(instance == null)
        {
            instance = new GeminiManager();
        }

        return instance.getInstance();
    }
}
