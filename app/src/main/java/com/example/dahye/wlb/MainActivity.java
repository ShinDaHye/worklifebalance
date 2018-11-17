package com.example.dahye.wlb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private ListView listView;
    private CategoryAdapter_main adapter;
    List<CategoryItem> Array = new ArrayList<CategoryItem>();

    Button submit, intent_add,intent_tuto, intent_graph;
    TextView providerId;
    TextView scoreTextView;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView=(TextView)findViewById(R.id.total_score);

        listView = (ListView) findViewById(R.id.main_categories);

        intent_add = (Button) findViewById(R.id.intent_add);
        intent_tuto = (Button) findViewById(R.id.intent_tuto);
        intent_graph = (Button)findViewById(R.id.intent_graph);
        providerId = (TextView) findViewById(R.id.providerId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
        String strToday = sdf.format(c1.getTime());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            providerId.setText(user.getEmail());
            id = user.getEmail().substring(0,user.getEmail().indexOf("@"));
        }else{
            providerId.setText("");
        }
        if(providerId.getText()==""){
            intent_add.setVisibility(View.GONE);
        }else{
        }

        intent_add.setOnClickListener(this);
        intent_tuto.setOnClickListener(this);
        intent_graph.setOnClickListener(this);

        if(id != null){
            initDatabase(id,strToday);
        }
    }
    private void initDatabase(final String id, final String day) {
        submit = (Button) findViewById(R.id.submit);
        mDatabase = FirebaseDatabase.getInstance();

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
    public void onClick(View view) {
        if(view.getId()==R.id.intent_add){
            Intent intent = new Intent(this,addcategory.class);
            startActivity(intent);
        }else if(view.getId()==R.id.intent_tuto){
            Intent intent = new Intent(this,addcategory.class);
            startActivity(intent);
        }else if(view.getId()==R.id.intent_graph){
            Intent intent = new Intent(this,graph.class);
            startActivity(intent);
        }else{
            finish();
        }
    }

}
