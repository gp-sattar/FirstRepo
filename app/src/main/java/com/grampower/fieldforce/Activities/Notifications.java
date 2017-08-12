package com.grampower.fieldforce.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.grampower.fieldforce.Adapters.NotificationRecyclerAdapter;
import com.grampower.fieldforce.Databases.DataBase;
import com.grampower.fieldforce.R;
import com.grampower.fieldforce.pojos.NotificationWrapper;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.List;



public class Notifications extends AppCompatActivity {


    List<NotificationWrapper> notificationList;
    RecyclerView mRecyclerView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        context=this;
        mRecyclerView=(RecyclerView)findViewById(R.id.notification_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotification();
    }

    void loadNotification() {
        DataBase ob = new DataBase(context);
        Cursor cursor = ob.getNotifications(ob);
        if (cursor.moveToFirst()) {
            do {
                String key=cursor.getString(1);
                String notification = cursor.getString(2);
                String diff=getTimeDifference(key);
                notificationList.add(new NotificationWrapper(diff,notification));
            } while (cursor.moveToNext());
            NotificationRecyclerAdapter adapter=new NotificationRecyclerAdapter(notificationList,context);
            mRecyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(context, "No Notification are  available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public String getTimeDifference(String notifytime){
        String timeDiff;
        long date=System.currentTimeMillis();
        SimpleDateFormat timeStampFormat=new SimpleDateFormat("yyMMddHHmmss");
        String timeStamp=timeStampFormat.format(date);

        LocalDateTime dateTime = LocalDateTime.parse(notifytime, DateTimeFormat.forPattern("yyMMddHHmmss"));
        LocalDateTime dateTime2 = LocalDateTime.now();
        Years diffInYears=Years.yearsBetween(dateTime, dateTime2);
        Months diffInMoths=Months.monthsBetween(dateTime, dateTime2);
        Days diffInDays = Days.daysBetween(dateTime, dateTime2);
        Hours diffInHours=Hours.hoursBetween(dateTime, dateTime2);
        Minutes diffInMinutes=Minutes.minutesBetween(dateTime, dateTime2);
        Seconds diffInSeconds=Seconds.secondsBetween(dateTime, dateTime2);

        if(diffInYears.getYears()!=0){
            timeDiff=diffInYears.getYears()+" years ago";

        }else if(diffInMoths.getMonths()!=0){
            timeDiff=diffInMoths.getMonths()+" months ago";

        }else if(diffInDays.getDays()!=0){
            timeDiff=diffInDays.getDays()+" days ago";

        }else if(diffInHours.getHours()!=0){
            timeDiff=diffInHours.getHours()+" hours ago";

        }else if(diffInMinutes.getMinutes()!=0){
            timeDiff=diffInMinutes.getMinutes()+" minutes ago";

        }else if(diffInSeconds.getSeconds()!=0){
            timeDiff=diffInSeconds.getSeconds()+" seconds ago";
        }else{
            timeDiff="just now";
        }
        return timeDiff;

    }


}
