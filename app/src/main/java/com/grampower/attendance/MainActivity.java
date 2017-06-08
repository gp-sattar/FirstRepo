package com.grampower.attendance;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends FragmentActivity {

    Context context;
    public static final String SCHEME = "content";
    public static final String AUTHORITY = "om.grampower.attendance.syncAdapters.stubProvider";
    public static final String TABLE_PATH = "profile";
    public static final String ACCOUNT_TYPE = "grampower.com";
    public static final String ACCOUNT = "sattar78692md";
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;

    Account mAccount;
    ContentResolver mResolver;
    GridView mGridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
         String[] menulist={"Register","Login","Task View", "Task Assign","Attendance","Profile"};
        int[] iconlist={R.drawable.attend,
                R.drawable.attend,
                R.drawable.attend,
                R.drawable.attend,
                R.drawable.attend,
                R.drawable.attend };

        mGridView=(GridView)findViewById(R.id.gridLayout);
        setGridAdapter(menulist,iconlist);
        mAccount = CreateSyncAccount(this);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch(position){

                    case 0:
                        startActivity(new Intent(context,SignUp.class));
                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(context,LoginActivity.class));
                        finish();
                        break;
                    case 2:
                        startActivity(new Intent(context,GetTodayTasks.class));
                        finish();
                        break;
                    case 3:
                        startActivity(new Intent(context,employeeListing.class));
                        finish();
                        break;
                    case 4:
                        startActivity(new Intent(context,selfieAttendance.class));
                        finish();
                        break;
                    case 5:
                        startActivity(new Intent(context,profile.class));
                        finish();
                        break;
                }
            }
        });

        mResolver = getContentResolver();
        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);

    }
     void setGridAdapter(String[] menu,int[] icons){
         GridViewAdapter adapter=new GridViewAdapter(context,menu,icons);
         mGridView.setAdapter(adapter);
     }

    public static Account CreateSyncAccount(Context context) {

        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);

        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);

       if (accountManager.addAccountExplicitly(newAccount, null, null)) {

        } else {

        }
        return newAccount;
    }


}
