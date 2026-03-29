package com.example.what_a_vacation_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity
{
    Button buttonLogIn, buttonSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogIn = findViewById(R.id.buttonLogIn);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        checkUserLoggedIn();

        buttonLogIn.setOnClickListener(view -> {
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
        });

        buttonSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
        });
    }


    // In case the user is logged in, he would be redirected to the trips' layout screen
    public void checkUserLoggedIn()
    {
        if(Firebase.firebaseAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(this, tripsLayout.class);
            startActivity(intent);
            finish();
        }
    }
}