package com.example.dahye.wlb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class graph extends Activity implements View.OnClickListener{
    private LineChart lineChart;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
//    List<Entry> array = new ArrayList<>();
    Button intent_result;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);

        lineChart = (LineChart) findViewById(R.id.linechart);
        intent_result = (Button) findViewById(R.id.intent_result) ;
        intent_result.setOnClickListener(this);
//        array =  initDatabase("sdhdonna");
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("total-score").child("sdhdonna");
        mReference.limitToLast(7).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Entry> array = new ArrayList<>();
              for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    int score = Integer.parseInt(messageData.getValue().toString());
                    int day = Integer.parseInt(messageData.getKey().toString());
                    array.add(new Entry(day, score));
                }
                LineDataSet lineDataSet = new LineDataSet(array, "오늘의 워라밸");
                lineDataSet.setLineWidth(2);
                lineDataSet.setCircleRadius(6);
                lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));;
                lineDataSet.setDrawCircleHole(true);
                lineDataSet.setDrawCircles(true);
                lineDataSet.setDrawHorizontalHighlightIndicator(false);
                lineDataSet.setDrawHighlightIndicators(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);

                YAxis yLAxis = lineChart.getAxisLeft();
                yLAxis.setTextColor(Color.BLACK);
                yLAxis.setDrawGridLines(false);

                YAxis yRAxis = lineChart.getAxisRight();
                yRAxis.setDrawLabels(false);
                yRAxis.setDrawAxisLine(true);
                yRAxis.setDrawGridLines(false);

                Description description = new Description();
                description.setText("");

                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setDescription(description);
                lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
                lineChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        lineChart = (LineChart) findViewById(R.id.linechart);
        //차트 데이터 지정
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1,1));
        entries.add(new Entry(2,-2));
        entries.add(new Entry(3,0));
        entries.add(new Entry(4,4));
        entries.add(new Entry(5,3));
        entries.add(new Entry(6,3));
        entries.add(new Entry(7,2));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.intent_result){
            Intent intent = new Intent(this, resultpage.class);
            startActivity(intent);
        }
    }
}
