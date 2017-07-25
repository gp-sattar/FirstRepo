package com.grampower.attendance.Others;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import com.grampower.attendance.Databases.DataBase;

/**
 * Created by sam on 14/7/17.
 */

public class FieldForce extends Application {


    public static FieldForce singleton;
    Context context;

    public static FieldForce getInstance() {
        return singleton;
    }


    void finish() {
        singleton.finish();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        context = this;
    }

    public void setAccountDetails(String email, String password, String name, String url) {
        SharedPreferences sharedPreferences = getSharedPreferences("MYPREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("date", "welcome");
        editor.putString("startAttendance", "noAttendance");
        editor.putString("endAttendance", "noAttendance");
        editor.putString("taskSync", "newuser");
        editor.putString("name", name);
        editor.putString("profileUrl", url);
        editor.commit();
    }


    public String getEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("MYPREFERENCES", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        return email;

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Bitmap getProfileBitmap() {
        Bitmap imageBitmap = null;
        DataBase dataBase = new DataBase(context);
        Cursor crsr = dataBase.getProfileData(dataBase);
        if (crsr.moveToFirst()) {
            String image_string = crsr.getString(5);
            byte[] image_data = Base64.decode(image_string, Base64.DEFAULT);
            if (image_data != null) {
                imageBitmap = BitmapFactory.decodeByteArray(image_data, 0, image_data.length);
            }
        }
        return imageBitmap;
    }

}
