package com.grampower.attendance.syncAdapters;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by samdroid on 6/6/17.
 */

public class syncService extends Service {

    private static SyncAdapter sSyncAdapter = null;

    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("az","syncService");

        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                Log.d("az","in Adpapter lock");
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("az","syncAdapterBind");
        return  sSyncAdapter.getSyncAdapterBinder();
    }

}

