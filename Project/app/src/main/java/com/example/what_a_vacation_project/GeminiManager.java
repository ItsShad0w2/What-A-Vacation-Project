package com.example.what_a_vacation_project;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.GenerateContentResponse;


import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class GeminiManager
{
    private static GeminiManager instance;
    private final GenerativeModel gemini;

    public GeminiManager()
    {
        gemini = new GenerativeModel("gemini-2.5-flash", BuildConfig.GeminiAPIKey);
    }

    public static GeminiManager getInstance()
    {
        if(instance == null)
        {
            instance = new GeminiManager();
        }

        return instance;
    }

    public void generateTrip(String prompt, GeminiCallBack callBack)
    {
        gemini.generateContent(prompt, new Continuation<GenerateContentResponse>()
        {
            @NonNull
            @Override
            public CoroutineContext getContext()
            {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object response)
            {
                if(response instanceof Result.Failure)
                {
                    callBack.onFailure(((Result.Failure) response).exception);
                }
                else
                {
                    callBack.onSuccess(((GenerateContentResponse) response).getText());
                }
            }
        });
    }
}
