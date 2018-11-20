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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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

public class graph extends Activity implements View.OnClickListener{
    private LineChart lineChart;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    List<Entry> xvals = new ArrayList<>();
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getEmail().substring(0,user.getEmail().indexOf("@"));
        line_graph_Database(id,7);
    }

    @Override
    public void onClick(View view) {

    }
    private void line_graph_Database(final String id, final int day_count) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();

        final int intToday = Integer.parseInt(sdf.format(c1.getTime()));
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("total-score").child(id);

        final String[] days = new String[]{"6일전","5일전","4일전","3일전","2일전","1일전","오늘"};

        mReference.limitToLast(day_count).addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    int score = Integer.parseInt(messageData.getValue().toString());
                    int day = Integer.parseInt(messageData.getKey().toString());
//                    xvals.add(new Entry(day-intToday, score));
                    xvals.add(new Entry(i, score));
                    days[i] = messageData.getKey().toString().substring(4,6)+"/"+messageData.getKey().toString().substring(6);
                    i++;
                }
                lineChart = (LineChart) findViewById(R.id.linechart);

                LineDataSet lineDataSet = new LineDataSet(xvals, "오늘의 워라밸");
                lineDataSet.setLineWidth(2);
                lineDataSet.setCircleRadius(6);
                lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
                lineDataSet.setDrawCircleHole(true);
                lineDataSet.setDrawCircles(true);
                lineDataSet.setDrawHorizontalHighlightIndicator(false);
                lineDataSet.setDrawHighlightIndicators(false);
                lineDataSet.setDrawValues(false);
//                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);

                IAxisValueFormatter formatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return days[(int)value];
                    }
                };
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(formatter);
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
