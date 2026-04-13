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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity
{
    EditText editTextEmail, editTextPassword, editTextName;
    Button signUp, logIn;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        signUp = findViewById(R.id.signUp);
        logIn = findViewById(R.id.logIn);
        
        signUp.setOnClickListener(View -> {
            signUp();
        });

        logIn.setOnClickListener(View -> {
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
            finish();
        });

    }

    public void signUp()
    {
        // Signing up the user to the application and creating a new user in the database

        String email = this.editTextEmail.getText().toString();
        String password = this.editTextPassword.getText().toString();
        String name = this.editTextName.getText().toString();

        if(name.isEmpty() || email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Please make sure all of the fields are filled", Toast.LENGTH_SHORT).show();
        }
        else 
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Connecting..");
            progressDialog.setMessage("Creating the user..");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() 
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) 
                {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) 
                    {
                        userId = firebaseAuth.getCurrentUser().getUid();
                        User newUser = new User(userId, name);
                        Firebase.referenceUser.child(userId).setValue(newUser);

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null)
                        {
                            Intent intent = new Intent(SignUp.this, TripsLayout.class);
                            startActivity(intent);
                            finish();
                        }
                    } 
                    else 
                    {
                        Exception exception = task.getException();

                        if (exception instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            Toast.makeText(SignUp.this, "Invalid credentials, make sure you've entered valid email and password", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if (exception instanceof FirebaseAuthUserCollisionException)
                            {
                                Toast.makeText(SignUp.this, "User already exists", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if (exception instanceof FirebaseNetworkException)
                                {
                                    Toast.makeText(SignUp.this, "Make sure you're connected to an internet service", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(SignUp.this, "An error has occurred, please try later to sign up", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}