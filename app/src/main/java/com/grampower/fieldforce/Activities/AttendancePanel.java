package com.grampower.fieldforce.Activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.grampower.fieldforce.R;

import java.util.ArrayList;

/**
 * Created by sam on 21/7/17.
 */

public class AttendancePanel extends AppCompatActivity {


   PieChart  mAttendancePanel;

    public static final int[] Colors = {
            Color.rgb(2094, 8, 25), Color.rgb(241, 155, 44), Color.rgb(255, 88, 48), Color.rgb(228, 126, 48), Color.rgb(183, 149, 11), Color.rgb(169, 50, 38), Color.rgb(148, 49, 38)
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_attendance);

        mAttendancePanel=(PieChart)findViewById(R.id.attendance_panel);

        setAttendancePanel();
    }


    void setAttendancePanel(){

        ArrayList<Entry> yValues= new ArrayList<Entry>();

        for (int i = 0; i < 4; i++) {
            yValues.add(new Entry(25, i));
        }
        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("Start Day");
        xValues.add("Start Work");
        xValues.add("End Work");
        xValues.add("End Day");
        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(10);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : Colors){
            colors.add(c);
        }
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        PieData data = new PieData(xValues, dataSet);
        data.setValueTextSize(11f);

        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(15);
        mAttendancePanel.setData(data);
        mAttendancePanel.setDescription(null);
        mAttendancePanel.setUsePercentValues(false);
        mAttendancePanel.setRotationEnabled(false);
        mAttendancePanel.highlightValues(null);
        mAttendancePanel.setRotationAngle(225);
        mAttendancePanel.setCenterTextTypeface(Typeface.createFromAsset(getAssets(),"fonts/neo_sans_bold.ttf"));
        mAttendancePanel.setDrawSliceText(true);
        //int color= Color.parseColor("#1976d2");
      //  mAttendancePanel.setHoleColor(color);
        mAttendancePanel.setDrawCenterText(true);
        mAttendancePanel.setCenterText("Attendance");
        mAttendancePanel.setCenterTextColor(Color.BLACK);
        mAttendancePanel.setCenterTextSize(20);
        mAttendancePanel.getLegend().setEnabled(false);
        mAttendancePanel.setDrawCenterText(true);
        // refresh/update pie chart
        mAttendancePanel.invalidate();

        // animate piechart
       // mAttendancePanel.animateXY(1400, 1400);

    }
}
