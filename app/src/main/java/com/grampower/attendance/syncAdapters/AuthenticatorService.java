package com.grampower.attendance.syncAdapters;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by samdroid on 8/6/17.
 */

public class AuthenticatorService extends Service {

    private stubAuthenticator mAuthenticator;

    public AuthenticatorService() {
        mAuthenticator = new stubAuthenticator(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}


