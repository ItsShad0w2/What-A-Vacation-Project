package com.example.what_a_vacation_project;

import static com.example.what_a_vacation_project.Firebase.firebaseAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity
{
    EditText editTextEmail, editTextPassword, editTextName;
    Button signIn, logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        signIn = findViewById(R.id.signIn);
        logIn = findViewById(R.id.logIn);
        
        signIn.setOnClickListener(View -> {
            signIn();
        });

        logIn.setOnClickListener(View -> {
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
        });

    }

    public void signIn()
    {
        String email = this.editTextEmail.getText().toString();
        String password = this.editTextPassword.getText().toString();
        String name = this.editTextName.getText().toString();

        if(email.isEmpty() || password.isEmpty())
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
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null)
                        {
                            Toast.makeText(SignIn.this, "Welcome to the application", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignIn.this, tripsLayout.class);
                            startActivity(intent);
                        }
                    } 
                    else 
                    {
                        Exception exception = task.getException();

                        if (exception instanceof FirebaseAuthInvalidUserException)
                        {
                            Toast.makeText(SignIn.this, "Invalid email address, please change it", Toast.LENGTH_SHORT).show();
                        } 
                        else if (exception instanceof FirebaseAuthWeakPasswordException) 
                        {
                            Toast.makeText(SignIn.this, "The password entered is too weak, please change it", Toast.LENGTH_SHORT).show();
                        } 
                        else if (exception instanceof FirebaseAuthUserCollisionException)
                        {
                            Toast.makeText(SignIn.this, "User already exists", Toast.LENGTH_SHORT).show();
                        } 
                        else if (exception instanceof FirebaseAuthInvalidCredentialsException) 
                        {
                            Toast.makeText(SignIn.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        } 
                        else if (exception instanceof FirebaseNetworkException) 
                        {
                            Toast.makeText(SignIn.this, "Make sure you're connected to an internet service", Toast.LENGTH_SHORT).show();
                        } 
                        else
                        {
                            Toast.makeText(SignIn.this, "An error has occurred, please try later to sign up", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}