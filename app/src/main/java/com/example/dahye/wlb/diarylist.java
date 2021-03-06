package com.example.dahye.wlb;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class diarylist extends AppCompatActivity  {
    private Spinner mSpinner;
    private List<String> list = new ArrayList<String>();
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private LinearLayout mResult;
    private Toolbar myToolbar;
    private ImageButton menuBtn;

    //결과보고서 뷰
    PieChart pieChart;
    List<PieEntry> yvalues= new ArrayList<>();

    TextView diary_content;
    String date;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diarylist);

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
                            default:
                                break;
                        }
                        return false;
                    }
                });

                MenuItem hideItem = (MenuItem) p.getMenu().getItem(3);
                hideItem.setVisible(false);
                p.show(); // 메뉴를 띄우기
            }
        });

        mResult = (LinearLayout)findViewById(R.id.result);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String id = user.getEmail().substring(0, user.getEmail().indexOf("@"));

        mReference = mDatabase.getReference("diary").child(id); // 변경값을 확인할 child 이름

        mReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
//                adapter.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String day = messageData.getKey().toString();
//                    String content = messageData.getValue().toString();
//                    adapter.add(day);
                    list.add(day);
                }
//                adapter.notifyDataSetChanged();
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(com.example.dahye.wlb.diarylist.this, android.R.layout.simple_spinner_dropdown_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(dataAdapter);
            }

            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ImageButton mbtn = (ImageButton)findViewById(R.id.btn);
        diary_content = (TextView) findViewById(R.id.diary_content);

        mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = mSpinner.getSelectedItem().toString();
                mReference = mDatabase.getInstance().getReference("diary").child(id).child(date);
                mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String msg = dataSnapshot.getValue().toString();
                        diary_content.setText(msg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //그래프 만들기
                make_graph_Database(id, date);
                yvalues.clear();
                mResult.setVisibility(View.VISIBLE);
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
}