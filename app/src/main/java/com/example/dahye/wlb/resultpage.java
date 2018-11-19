package com.example.dahye.wlb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class resultpage extends Activity implements View.OnClickListener {
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
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar c1 = Calendar.getInstance();
            strToday = sdf.format(c1.getTime());
            intent_diary.setVisibility(View.GONE);
            diary_content.setVisibility(View.GONE);

        }
        //그래프 만들기
        make_graph_Database(id,strToday);


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

        //제출버튼 클릭 이벤트
        submit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = diary.getText().toString();
                mReference = FirebaseDatabase.getInstance().getReference("diary");
                mReference.child(id).child(strToday).setValue(note);
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
}
