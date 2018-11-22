package com.example.dahye.wlb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
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

    private Toolbar myToolbar;
    private ListView listView;
    private CategoryAdapter_main adapter;

    private DefaultDataSet defaultDataSet;

    private long time2 = 0;

    Button submit;
    ImageButton menuBtn;
    TextView workScoreTextView,lifeScoreTextView;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");

        menuBtn = (ImageButton)findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu p = new PopupMenu(getApplicationContext(), view,Gravity.LEFT);
                getMenuInflater().inflate(R.menu.nav_menu, p.getMenu());

                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.redirect_addcategory:
                                intent = new Intent(getApplicationContext(),addcategory.class);
                                startActivity(intent);
                                break;
                            case R.id.redirect_alarm:
                                intent = new Intent(getApplicationContext(),SetAlarm.class);
                                startActivity(intent);
                                break;
                            case R.id.redirect_diary:
                                intent = new Intent(getApplicationContext(),diarylist.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });

                MenuItem hideItem = (MenuItem) p.getMenu().getItem(0);
                hideItem.setVisible(false);
                p.show(); // 메뉴를 띄우기
            }
        });

        workScoreTextView=(TextView)findViewById(R.id.work_score);
        lifeScoreTextView=(TextView)findViewById(R.id.life_score);

        listView = (ListView) findViewById(R.id.main_categories);

        listView.setVerticalScrollBarEnabled(false);

        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        id = user.getEmail().substring(0,user.getEmail().indexOf("@"));

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
                int workScore = 0;
                int lifeScore = 0;
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
                    if(Integer.parseInt(item.getScore())>0){
                        lifeScore += Integer.parseInt(item.getScore())*Integer.parseInt(item.getUnit());
                        lifeScoreTextView.setText(lifeScore +"");
                    }else if(Integer.parseInt(item.getScore())<0){
                        workScore += Integer.parseInt(item.getScore())*Integer.parseInt(item.getUnit());
                        workScoreTextView.setText(workScore+"");
                    }
                    totalScore += Integer.parseInt(item.getScore())*Integer.parseInt(item.getUnit());
                }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
