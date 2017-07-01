package com.grampower.attendance;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

    Context context;

    public static final String AUTHORITY = "com.grampower.attendance.syncAdapters";
    public static final String ACCOUNT_TYPE = "com.grampower";
    public static final String ACCOUNT = "dummyaccount";
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

    Account mAccount;
    ContentResolver mResolver;
    GridView mGridView;
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        String[] menulist = {"Attendance", "Profile", "Task View"};
        int[] iconlist = {R.drawable.attendc,
                R.drawable.user_icon,
                R.drawable.task_icon,
        };

        startSyncAdapter();

        mWebView = (WebView) findViewById(R.id.marquee);
        mGridView = (GridView) findViewById(R.id.gridLayout);
        setGridAdapter(menulist, iconlist);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {

                    case 0:
                        startActivity(new Intent(context, selfieAttendance.class));
                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(context, profile.class));
                        finish();
                        break;
                    case 2:
                        startActivity(new Intent(context, CalenderDatePicker.class));
                        finish();
                        break;
                }
            }
        });

        loadNotification();

    }

    void setGridAdapter(String[] menu, int[] icons) {
        GridViewAdapter adapter = new GridViewAdapter(context, menu, icons);
        mGridView.setAdapter(adapter);
    }

    void loadNotification() {
        DataBase ob = new DataBase(context);
        Cursor cursor = ob.getNotifications(ob);
        String notifications = "";
        if (cursor.moveToFirst()) {
            do {
                String notification = cursor.getString(2);
                notifications = notifications + "<marquee  style='margin-bottom:10px; 'behavior='scroll' direction='left' scrollamount=3><span style='font-size:100%;color:red;'>&starf;</span>"
                        + notification + "</marquee>";

            } while (cursor.moveToNext());
            String summary = "<html><div style='border:3px solid #ffa500;'><table><tr><div style='height:30px;background-color:#ffa500'><text style='gravity:center;'>Current</text></div></tr><tr><FONT color='#000000'>" + notifications + "</FONT></tr></table></div></html>";
            mWebView.loadData(summary, "text/html", "utf-8");

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


}
