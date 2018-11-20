package com.example.dahye.wlb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity  {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseUser user;

    private ListView listView;
    private CategoryAdapter_main adapter;

    private DefaultDataSet defaultDataSet;

    private long time2 = 0;

    Button submit;
    TextView providerId;
    TextView scoreTextView;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView=(TextView)findViewById(R.id.total_score);
        listView = (ListView) findViewById(R.id.main_categories);
        providerId = (TextView) findViewById(R.id.providerId);

        listView.setVerticalScrollBarEnabled(false);

        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            providerId.setText(user.getEmail());
            id = user.getEmail().substring(0,user.getEmail().indexOf("@"));
        }else{
            providerId.setText("");
        }

        defaultDataSet = new DefaultDataSet();
        defaultDataSet.ckFirstVisit();
        defaultDataSet.existenceCheck();

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
                List<CategoryItem> Array = new ArrayList<CategoryItem>();
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
                        Intent intent = new Intent(getApplicationContext(),resultpage.class);
                        mDatabase.getReference("total-score").child(id).child(day).setValue(finalTotalScore);
                        startActivity(intent);
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
        MenuItem item1 = (MenuItem) menu.findItem(R.id.redirect_main);
        item1.setVisible(false);
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
               /* intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;*/
                return super.onOptionsItemSelected(item);
            case R.id.redirect_addcategory:
                intent = new Intent(this,addcategory.class);
                startActivity(intent);
                return true;
            case R.id.redirect_alarm:
                intent = new Intent(this,SetAlarm.class);
                startActivity(intent);
                return true;
            case R.id.redirect_diary:
                intent = new Intent(this,diarylist.class);
                startActivity(intent);
                return true;
            case R.id.redirect_test:
                intent = new Intent(this,Testview.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed(){
        if(System.currentTimeMillis()-time2>=2000){
            time2 = System.currentTimeMillis();
            Toast.makeText(this,"\'뒤로\' 버튼을 한번 더 누르면 종료합니다!",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time2<2000){
            finishAffinity();
        }
    }
}
