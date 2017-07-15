package com.grampower.attendance.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.darwindeveloper.onecalendar.clases.Day;
import com.darwindeveloper.onecalendar.views.OneCalendarView;
import com.grampower.attendance.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalenderDatePicker extends AppCompatActivity {


    ImageButton mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_date_picker);

          mBack=(ImageButton)findViewById(R.id.backDatePicker);
          mBack.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(CalenderDatePicker.this,MainActivity.class));
                  finish();
              }
          });

         OneCalendarView calendarView = (OneCalendarView) findViewById(R.id.oneCalendar);
        calendarView.setOnCalendarChangeListener(new OneCalendarView.OnCalendarChangeListener() {
            @Override
            public void prevMonth() {
                //hacer algo aqui
            }

            @Override
            public void nextMonth() {
                //hacer algo aqui
            }
        });

        calendarView.setOneCalendarClickListener(new OneCalendarView.OneCalendarClickListener() {

            @Override
            public void dateOnClick(Day day, int position) {

                Date date=day.getDate();
                SimpleDateFormat dateFormat=new SimpleDateFormat("dd_MM_yy");
                  String dateText=dateFormat.format(date);

                    Intent intent=new Intent(CalenderDatePicker.this,GetTodayTasks.class);
                intent.putExtra("date",dateText);
                   startActivity(new Intent(intent));
                finish();

            }


            @Override
            public void dateOnLongClick(Day day, int position) {
                //
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CalenderDatePicker.this,MainActivity.class));
    }
}
