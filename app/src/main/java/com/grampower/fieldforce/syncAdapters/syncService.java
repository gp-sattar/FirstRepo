package com.grampower.fieldforce.syncAdapters;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by samdroid on 6/6/17.
 */

public class syncService extends Service {

    private static SyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true, true);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

}

