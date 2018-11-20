package com.example.dahye.wlb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class diarylist extends AppCompatActivity {
    private ListView diarylist;
    private ArrayAdapter<String> adapter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diarylist);

        diarylist = (ListView)findViewById(R.id.diary_list) ;
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final String id = user.getEmail().substring(0,user.getEmail().indexOf("@"));

        mReference = mDatabase.getReference("diary").child(id); // 변경값을 확인할 child 이름

        mReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String day = messageData.getKey().toString();
//                    String content = messageData.getValue().toString();
                    adapter.add(day);
                }
                adapter.notifyDataSetChanged();

            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        diarylist.setAdapter(adapter);
        diarylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), resultpage.class);
                intent.putExtra("date",adapterView.getAdapter().getItem(i).toString());
//                Intent intent = getIntent();
//                String name = intent.getStringExtra("date");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent =null;

        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(getApplicationContext(), login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        }

        switch (item.getItemId()){
            case R.id.redirect_main:
                this.finish();
            case R.id.redirect_addcategory:
                intent = new Intent(this,addcategory.class);
                startActivity(intent);
                this.finish();
            case R.id.redirect_alarm:
                intent = new Intent(this,SetAlarm.class);
                startActivity(intent);
                this.finish();
            case R.id.redirect_diary:
/*                intent = new Intent(this,diarylist.class);
                startActivity(intent);
                this.finish();*/
                return super.onOptionsItemSelected(item);
            case R.id.redirect_test:
                intent = new Intent(this,Testview.class);
                startActivity(intent);
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
