package com.example.dahye.wlb;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class resultpage extends AppCompatActivity implements View.OnClickListener {
    PieChart pieChart;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    List<PieEntry> yvalues= new ArrayList<>();

    Button submit_image, intent_diary;
    EditText diary;
    TextView diary_content;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultpage);
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        final String strToday;

        diary = (EditText)findViewById(R.id.diary);
        diary_content = (TextView)findViewById(R.id.diary_content);
        intent_diary = (Button)findViewById(R.id.intent_diary);
        submit_image = (Button)findViewById(R.id.submit_image);

        intent_diary.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String id = user.getEmail().substring(0,user.getEmail().indexOf("@"));

        if(date != null){
            strToday = date;
            submit_image.setVisibility(View.GONE);
            diary.setVisibility(View.GONE);

            mReference = mDatabase.getReference().child("diary").child(id).child(strToday);
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
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar c1 = Calendar.getInstance();
            strToday = sdf.format(c1.getTime());
            intent_diary.setVisibility(View.GONE);
            diary_content.setVisibility(View.GONE);


        }
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
                    int yval = Integer.parseInt(msg2);
                    yvalues.add(new PieEntry(yval,msg1));
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
        if(view.getId()==R.id.intent_diary){
            Intent intent = new Intent(this, diarylist.class);
            startActivity(intent);
        }else{
            finish();
        }
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
                intent = new Intent(this,diarylist.class);
                startActivity(intent);
                this.finish();
            case R.id.redirect_test:
                intent = new Intent(this,Testview.class);
                startActivity(intent);
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
