package com.example.dahye.wlb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseUser user;

    private ListView listView;
    private CategoryAdapter_main adapter;
    List<CategoryItem> Array = new ArrayList<CategoryItem>();

    Button submit;
    TextView providerId;
    TextView scoreTextView;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        scoreTextView=(TextView)findViewById(R.id.total_score);
        listView = (ListView) findViewById(R.id.main_categories);
        providerId = (TextView) findViewById(R.id.providerId);

        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            providerId.setText(user.getEmail());
            id = user.getEmail().substring(0,user.getEmail().indexOf("@"));
        }else{
            providerId.setText("");
        }

        existenceCheck();

        if(id != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar c1 = Calendar.getInstance();
            String strToday = sdf.format(c1.getTime());

            initDatabase(id,strToday);
        }
    }
    private void initDatabase(final String id, final String day) {
        submit = (Button) findViewById(R.id.submit);

        mReference = mDatabase.getReference("split-score").child(id).child(day); // 변경값을 확인할 child 이름

        mReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalScore = 0;
                adapter.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String msg1 = messageData.getKey().toString();
                    String msg2 = messageData.child("score").getValue().toString();
                    String msg3 = messageData.child("unit").getValue().toString();
                    Array.add(new CategoryItem(msg1,msg2,msg3));
                    adapter.add(new CategoryItem(msg1,msg2,msg3));
                }

                for(CategoryItem item : Array){
                    totalScore += Integer.parseInt(item.getScore())*Integer.parseInt(item.getUnit());
                }

                scoreTextView.setText("총점 : " + Integer.toString(totalScore));

                final int finalTotalScore = totalScore;
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabase.getReference("total-score").child(id).child(day).setValue(finalTotalScore);
                    }
                });
            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new CategoryAdapter_main(this, R.layout.activity_main,R.id.main_category, new ArrayList<CategoryItem>());
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        switch (item.getItemId()){
            case R.id.redirect_addcategory:
                intent = new Intent(this,addcategory.class);
                startActivity(intent);
                return true;
            case R.id.redirect_graph:
                intent = new Intent(this,graph.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                String today = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

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
