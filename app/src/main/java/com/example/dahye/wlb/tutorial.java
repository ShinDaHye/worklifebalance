package com.example.dahye.wlb;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class tutorial extends Activity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

    private ListView listView;
    private CategoryAdapter adapter;
    List<CategoryItem> Array = new ArrayList<CategoryItem>();
    String id;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            id =user.getEmail();
            id = id.substring(0,id.indexOf("@"));
        }else{
            id = "sdhdonna";
            Toast.makeText(tutorial.this,"로그인해주세요",Toast.LENGTH_LONG).show();
        }
        listView = (ListView) findViewById(R.id.listviewmsg);

        initDatabase(id);
    }

    private void initDatabase(String id) {
        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("categories").child(id); // 변경값을 확인할 child 이름

        mReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String msg1 = messageData.getKey().toString();
                    String msg2 = messageData.child("score").getValue().toString();
                    Array.add(new CategoryItem(msg1,msg2));
                    adapter.add(new CategoryItem(msg1,msg2));
                }
            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new CategoryAdapter(this, R.layout.customlistview_category,R.id.category, new ArrayList<CategoryItem>());
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }
}
