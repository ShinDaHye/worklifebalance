package com.example.dahye.wlb;

import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class adding {

    public adding(){

    }
    public static void add(String id, FirebaseDatabase mDatabase, String cate, int score){
        DatabaseReference mReference = mDatabase.getReference();
        mReference.child("categories").child(id).child(cate).child("score").setValue(score);
    }
}
