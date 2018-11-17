package com.example.dahye.wlb;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

public class resultpage extends Activity {
    PieChart pieChart;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultpage);
        pieChart = (PieChart) findViewById(R.id.piechart);

        //enable dataset in percentage
        pieChart.setUsePercentValues(true);

        /*데이터셋 생성*/
        List<PieEntry> yvalues= new ArrayList<>();
        yvalues.add(new PieEntry(8f,0));
        yvalues.add(new PieEntry(15f,1));
        yvalues.add(new PieEntry(12f,2));
        yvalues.add(new PieEntry(25f,3));
        yvalues.add(new PieEntry(23f,4));
        yvalues.add(new PieEntry(17f,5));
        yvalues.add(new PieEntry(17f,6));

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
    }
