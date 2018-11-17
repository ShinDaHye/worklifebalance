package com.example.dahye.wlb;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class resultpage extends Activity {
    PieChart pieChart;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    List<PieEntry> yvalues= new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultpage);

        make_graph_Database("daltokkiman","day01");
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

}
