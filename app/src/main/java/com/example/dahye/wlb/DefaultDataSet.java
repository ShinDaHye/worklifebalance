package com.example.dahye.wlb;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DefaultDataSet {

    private FirebaseDatabase mDatabase;
    private FirebaseUser user;

    String id;
    String today;

    public DefaultDataSet(){
        mDatabase=FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        id = user.getEmail().substring(0,user.getEmail().indexOf("@"));
        today = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
    }

    public void ckFirstVisit(){
        DatabaseReference datacheckRef = mDatabase.getReference("user");
        datacheckRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(id).exists()) {
                    firstVisit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void firstVisit(){
        mDatabase.getReference("user").child(id).setValue(today);
        mDatabase.getReference("categories").child(id).child("-work").child("score").setValue(-10);
        mDatabase.getReference("categories").child(id).child("shopping").child("score").setValue(1);
        mDatabase.getReference("diary").child(id).child(today).setValue("");
        for(int i = 0; i<7; i++){
            mDatabase.getReference("total-score").child(id).child((Integer.parseInt(today)-i) +"").setValue(0);
        }
    }

    // 오늘 날짜의 데이터가 split-score에 존재하는지 확인.
    public void existenceCheck(){
        DatabaseReference datacheckRef = mDatabase.getReference("split-score").child(id);

        datacheckRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String today = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(today).exists()){
                    setDefaultUnit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 오늘 날짜의 데이터가 split-score에 존재하지 않는 경우 default로 생성
    public void setDefaultUnit(){
        String categories;
        DatabaseReference categoryRef = mDatabase.getReference("categories").child(id);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference storageLocRef = mDatabase.getReference("split-score").child(id);

                Map<String,Object> defaultUnits = new HashMap<>();
                Map<String,Object> defaultUnit = new HashMap<>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Map<String, Object> submap = new HashMap<>();
                    submap.put("score", data.child("score").getValue());
                    submap.put("unit", 0);
                    defaultUnit.put(data.getKey().toString(), submap);
                    defaultUnits.put(today,defaultUnit);
                }
                storageLocRef.updateChildren(defaultUnits);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
