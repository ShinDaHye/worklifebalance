package com.example.dahye.wlb;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import static java.lang.Math.abs;

public class resultpage extends AppCompatActivity implements View.OnClickListener {
    PieChart pieChart;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    List<PieEntry> yvalues= new ArrayList<>();

    private Toolbar myToolbar;

    Button submit_image;

    ImageButton menuBtn;
    EditText diary;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultpage);

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
                            case R.id.redirect_main:
                                finish();
                                break;
                            case R.id.redirect_addcategory:
                                intent = new Intent(getApplicationContext(),addcategory.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.redirect_alarm:
                                intent = new Intent(getApplicationContext(),SetAlarm.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.redirect_diary:
                                intent = new Intent(getApplicationContext(),diarylist.class);
                                startActivity(intent);
                                finish();
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


        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        final String strToday;

        diary = (EditText)findViewById(R.id.diary);
        submit_image = (Button)findViewById(R.id.submit_image);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String id = user.getEmail().substring(0,user.getEmail().indexOf("@"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
        strToday = sdf.format(c1.getTime());

        //그래프 만들기
        make_graph_Database(id,strToday);


        //제출버튼 클릭 이벤트
        submit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = diary.getText().toString();
                mReference = FirebaseDatabase.getInstance().getReference("diary");
                mReference.child(id).child(strToday).setValue(note);
                Intent intent = new Intent(getApplicationContext(),graph.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void make_graph_Database(final String id, String day) {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("split-score").child(id).child(day); // 변경값을 확인할 child 이름

        mReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String msg1 = messageData.getKey().toString();
                    String msg2 = messageData.child("score").getValue().toString();
                    String msg3 = messageData.child("unit").getValue().toString();
                    int yval = Integer.parseInt(msg2)*Integer.parseInt(msg3);
                    if(yval != 0){
                        yvalues.add(new PieEntry(abs(yval),msg1));
                    }
                }
                pieChart = (PieChart) findViewById(R.id.piechart);

                pieChart.setUsePercentValues(true);
                PieDataSet dataSet = new PieDataSet(yvalues,"오늘의 워라밸");
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

                PieData data = new PieData(dataSet);
                data.setValueTextSize(13f);
                data.setValueTextColor(Color.DKGRAY);
                pieChart.setData(data);
                //enable hole
                pieChart.setDrawHoleEnabled(true);
                pieChart.setHoleRadius(30f);
                pieChart.setTransparentCircleRadius(30f);
                pieChart.invalidate();//refresh
            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

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

        if(item.getItemId()==android.R.id.home){
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(getApplicationContext(), login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
