package com.example.what_a_vacation_project;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    ImageView backgroundMainActivity;
    Button buttonLogIn, buttonSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backgroundMainActivity = findViewById(R.id.backgroundMainActivity);
        buttonLogIn = findViewById(R.id.buttonLogIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // Blurring the background of the activity

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            backgroundMainActivity.setRenderEffect(RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.MIRROR));
        }

        checkUserLoggedIn();

        buttonLogIn.setOnClickListener(view -> {
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
        });

        buttonSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        });
    }


    // In case the user is logged in, he would be redirected to the trips' layout screen
    public void checkUserLoggedIn()
    {
        if(Firebase.firebaseAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(this, TripsLayout.class);
            startActivity(intent);
            finish();
        }
    }
}