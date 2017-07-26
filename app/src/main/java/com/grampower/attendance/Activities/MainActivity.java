package com.grampower.attendance.Activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.grampower.attendance.Adapters.GridViewAdapter;
import com.grampower.attendance.Adapters.NotificationRecyclerAdapter;
import com.grampower.attendance.Databases.DataBase;
import com.grampower.attendance.Others.FieldForce;
import com.grampower.attendance.R;
import com.grampower.attendance.pojos.NotificationWrapper;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    Context context;

    public static final String AUTHORITY = "com.grampower.attendance.syncAdapters";
    public static final String ACCOUNT_TYPE = "com.grampower";
    public static final String ACCOUNT = "dummyaccount";
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 30L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
   List<NotificationWrapper> notificationList;

    Account mAccount;
    ContentResolver mResolver;
    GridView mGridView;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        notificationList=new ArrayList<>();
        String[] menulist = {"Attendance", "Profile", "Task View"};
        int[] iconlist = {R.drawable.attendance_clicked, R.drawable.profile, R.drawable.tasks,};

         mRecyclerView=(RecyclerView)findViewById(R.id.notification_recycler);
         mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
         mGridView = (GridView) findViewById(R.id.gridLayout);

        loadNotification();
        setGridAdapter(menulist, iconlist);
        startSyncAdapter();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        if(!FieldForce.getInstance().isNetworkAvailable()){
                            startActivity(new Intent(context, OfflineAttendance.class));
                        }else{
                            startActivity(new Intent(context, selfieAttendance.class));
                        }

                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(context, profile.class));
                        finish();
                        break;
                    case 2:
                        //startActivity(new Intent(context, CalenderDatePicker.class));

                        startActivity(new Intent(context,TaskRelatedActions.class));
                        finish();
                        break;
                }
            }
        });


    }

    void setGridAdapter(String[] menu, int[] icons) {
        GridViewAdapter adapter = new GridViewAdapter(context, menu, icons);
        mGridView.setAdapter(adapter);
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


    void startSyncAdapter() {
        mAccount = CreateSyncAccount(getApplicationContext());
        mResolver = getContentResolver();
        ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);
    }


    public static Account CreateSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {

        } else {

        }
        return newAccount;
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
