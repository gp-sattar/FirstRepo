package com.grampower.attendance.syncAdapters;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by samdroid on 8/6/17.
 */

public class AuthenticatorService extends Service {

    private stubAuthenticator mAuthenticator;

    public AuthenticatorService() {

        Log.d("az","AuthenticatorService");
        mAuthenticator = new stubAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}


