package com.example.what_a_vacation_project;

import static com.example.what_a_vacation_project.Firebase.firebaseAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity
{

    EditText editTextEmail, editTextPassword;
    Button logIn, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        logIn = findViewById(R.id.logIn);
        signUp = findViewById(R.id.signUp);

        logIn.setOnClickListener(View -> {
            logIn();
        });

        signUp.setOnClickListener(View -> {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
            finish();
        });
    }

    public void logIn()
    {
        // Logging in the user to the application
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Make sure all of the fields are filled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Connecting..");
            progressDialog.setMessage("Logging in..");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    progressDialog.dismiss();
                    if (task.isSuccessful())
                    {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null)
                        {
                            Intent intent = new Intent(LogIn.this, TripsLayout.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else
                    {
                        Exception exception = task.getException();

                        if (exception instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            Toast.makeText(LogIn.this, "Invalid credentials, make sure you've entered valid email and password", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if (exception instanceof FirebaseNetworkException)
                            {
                                Toast.makeText(LogIn.this, "Make sure you're connected to an internet service", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(LogIn.this, "An error occurred. Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }
}