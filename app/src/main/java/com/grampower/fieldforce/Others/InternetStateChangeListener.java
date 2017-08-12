package com.grampower.fieldforce.Others;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by sam on 9/8/17.
 */

public class InternetStateChangeListener extends BroadcastReceiver {


    public static final String AUTHORITY = "com.grampower.attendance.syncAdapters";
    public static final String ACCOUNT_TYPE = "com.grampower";
    public static final String ACCOUNT = "dummyaccount";


    Account mAccount;

    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
     this.context=context;

        for(int i=0;i<10;i++){
            Log.d("az","Internet listener has triggered....................................................");
        }
       if(isNetworkAvailable()){
          startSyncAdapter();
       }
    }


    public void startSyncAdapter() {
       /* mAccount = CreateSyncAccount(context);
        ContentResolver mResolver = context.getContentResolver();
        ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);*/

        mAccount = CreateSyncAccount(context);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

    }


    public static Account CreateSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {

        } else {

        }
        return newAccount;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
