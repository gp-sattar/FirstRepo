package com.grampower.attendance;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    Context context;

    public static final String AUTHORITY = "com.grampower.attendance.syncAdapters";
    public static final String ACCOUNT_TYPE = "com.grampower.attendance.account";
    public static final String ACCOUNT = "dummyaccount@grampower.com";
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;

    Account mAccount;
    ContentResolver mResolver;
    GridView mGridView;
    WebView mWebView;

    List<NotificationWrapper> listOfNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
         String[] menulist={"Attendance","Profile","Task View"};
        int[] iconlist={R.drawable.attendc,
                R.drawable.user_icon,
                R.drawable.task_icon,
        };

        mWebView=(WebView)findViewById(R.id.marquee);
        mGridView=(GridView)findViewById(R.id.gridLayout);
        setGridAdapter(menulist,iconlist);

        mAccount = CreateSyncAccount(getApplicationContext());

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch(position){

                    case 0:
                        startActivity(new Intent(context,selfieAttendance.class));
                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(context,profile.class));
                        finish();
                        break;
                    case 2:
                        startActivity(new Intent(context,CalenderDatePicker.class));
                        finish();
                        break;
                }
            }
        });



        listOfNotification=new ArrayList<>();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Notifications");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key=dataSnapshot.getKey();
                String notifyLine=dataSnapshot.getValue().toString();
                NotificationWrapper notificationWrapper=new NotificationWrapper(key,notifyLine);
                listOfNotification.add(notificationWrapper);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        new waitForList().execute();

        mResolver = getContentResolver();
        Log.d("az","syncing call");
        ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(mAccount,AUTHORITY,true);
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);
    }

     void setGridAdapter(String[] menu,int[] icons){
         GridViewAdapter adapter=new GridViewAdapter(context,menu,icons);
         mGridView.setAdapter(adapter);
     }

    public static Account CreateSyncAccount(Context context) {

        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

       if (accountManager.addAccountExplicitly(newAccount, null, null)) {
               Log.d("az","if");
        } else {
           Log.d("az","else");
        }
        return newAccount;
    }



    class waitForList extends AsyncTask<Void,Void,Void> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog =new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading Notifications...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            while (listOfNotification.size() == 0) ;
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            String notifications="";
            for(int i=0;i<listOfNotification.size();i++){
                NotificationWrapper note=listOfNotification.get(i);
                notifications=notifications+"<marquee  style='margin-bottom:10px; 'behavior='scroll' direction='left' scrollamount=3><span style='font-size:100%;color:red;'>&starf;</span>"
                        + note.getNotification() + "</marquee>";
            }
            String summary = "<html><div style='border:3px solid #ffa500;'><table><tr><div style='height:30px;background-color:#ffa500'><text style='gravity:center;'>Current</text></div></tr><tr><FONT color='#000000'>"+notifications+"</FONT></tr></table></div></html>";
            mWebView.loadData(summary, "text/html", "utf-8");

        }

    }



    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

}
