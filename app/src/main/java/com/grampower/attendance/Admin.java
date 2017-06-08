package com.grampower.attendance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;
import android.widget.Toast;

public class Admin extends AppCompatActivity {

    CalendarView mCalendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mCalendarView=(CalendarView)findViewById(R.id.calender);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(Admin.this,dayOfMonth+"/"+month+"/"+year,Toast.LENGTH_LONG).show();
            }
        });

    }
}
