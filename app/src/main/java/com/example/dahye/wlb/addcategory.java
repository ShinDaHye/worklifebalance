package com.example.dahye.wlb;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addcategory extends Activity  implements View.OnClickListener
{
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    EditText Category, Score;
    Button addBtn;
    String id, name, sc ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcategory);
        mDatabase = FirebaseDatabase.getInstance();

        Category = (EditText) findViewById(R.id.category);
        Score = (EditText) findViewById(R.id.score);
        addBtn= (Button) findViewById(R.id.addbtn);
        name = Category.getText().toString();
        sc = Score.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            id =user.getEmail();
            id = id.substring(0,id.indexOf("@"));
        }else{
            Toast.makeText(addcategory.this,"로그인해주세요",Toast.LENGTH_LONG).show();
        }
        mReference = mDatabase.getReference();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReference.child("categories").child(id).child(Category.getText().toString()).child("score").setValue(Score.getText().toString());
            }
        });

    }
        @Override
    public void onClick(View view) {

    }

}
