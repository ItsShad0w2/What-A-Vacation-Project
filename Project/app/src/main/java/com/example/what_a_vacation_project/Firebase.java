package com.example.what_a_vacation_project;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase
{
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public static DatabaseReference referenceUser = firebaseDatabase.getReference("FBAT").child("User");
    public static DatabaseReference referenceTrip = firebaseDatabase.getReference("FBAT").child("User").child("Trip");

}
