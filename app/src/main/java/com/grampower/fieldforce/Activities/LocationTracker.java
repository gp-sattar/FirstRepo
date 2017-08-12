package com.grampower.fieldforce.Activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.grampower.fieldforce.Databases.DataBase;
import com.grampower.fieldforce.R;

import java.text.SimpleDateFormat;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by sam on 25/7/17.
 */

public class LocationTracker extends BroadcastReceiver {
    Location mLastLocation;
    Context context;
    Boolean isGPSEnabled,isNetworkEnabled;
    LocationManager mLocationManager;
    cGPSListener mGPSListner;
    static Boolean locationChangedFlag = false;
    private static final int GPS_RUNTIME_TIMEOUT_MS = 60000;
    private static final long MIN_TIME_BW_UPDATES = 0;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
     static int notificationSerial=0;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;


        createNotification("On receive");
        Log.d("az", "background  location tracker  On receive....");

        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            createNotification("enable GPS for tracking");
            return;
        }

        mGPSListner = new cGPSListener();
        mLocationManager.addGpsStatusListener(mGPSListner);

        getLocation();
        boolean gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gps_enabled) {

            new waitForLocation().execute();

        } else {
            createNotification("enable GPS for tracking");
        }

    }



    public LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d("az", "on location changed");
            locationChangedFlag = true;
            mLastLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("az", "On disabled");
        }
    };


    void getLocation(){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
           createNotification("enable GPS for tracking");
            return;
        }

        mGPSListner = new cGPSListener();
        mLocationManager.addGpsStatusListener(mGPSListner);

        if(isGPSEnabled){
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
            if (mLocationManager != null) {
                Location locate = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(locate!=null){
                    mLastLocation=locate;
                }
            }
        }

        if(isNetworkEnabled){
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
            if (mLocationManager != null) {
                Location networkLocate = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(networkLocate!=null){
                    mLastLocation=networkLocate;
                }
            }
        }

    }



    public void createNotification(String msg) {

        long date=System.currentTimeMillis();
        SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("HHmmss");

        String timeString=simpleTimeFormat.format(date);
        int id=Integer.parseInt(timeString+notificationSerial);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent,PendingIntent.FLAG_ONE_SHOT);

        Notification n = new Notification.Builder(context)
                .setContentTitle("Smart Meter")
                .setContentText(msg)
                .setSmallIcon(R.drawable.smalllogo)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(id, n);

        notificationSerial++;
    }


    class waitForLocation extends AsyncTask<Void, Void, Location> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("az", "in pre execution");
        }

        @Override
        protected Location doInBackground(Void... params) {

            Location mLocation = null;
            try {
                long startTime = System.currentTimeMillis();
                do {

                    if (mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Log.d("az", "location from gps background");
                    }


                    if (mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Log.d("az", "location from network background");
                    }

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        createNotification("enable GPS for tracking");
                        return null;
                    }

                    if (locationChangedFlag&&mLastLocation!=null) {
                        mLocation = mLastLocation;
                        locationChangedFlag = false;
                        Log.d("az", "location is not null");
                    }

                    if (mLocation != null && mLocation.getAccuracy() != 0 && mLocation.getAccuracy() < MIN_DISTANCE_CHANGE_FOR_UPDATES) {
                        Log.d("az", "break");
                        break;
                    }

                    Log.d("az", String.valueOf(mLocation) + " location from background");

                } while ((System.currentTimeMillis() - startTime < GPS_RUNTIME_TIMEOUT_MS));


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("az", " ex: " + e);
            }
            return mLocation;


        }

        @Override
        protected void onPostExecute(Location location) {
            super.onPostExecute(location);
            if(location!=null){
                Log.d("az", location.getLatitude() + " location from  post execute broadcaster " + location.getLongitude());
               long date=System.currentTimeMillis();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyy");
                SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("hh:mm a");
                String dateString=simpleDateFormat.format(date);
                String timeString=simpleTimeFormat.format(date);
                String locationCoordinates=location.getLatitude()+"_"+location.getLongitude();
                DataBase dataBase=new DataBase(context);
                dataBase.insertTrackedLocation(dataBase,dateString,timeString,locationCoordinates,0);
                createNotification("Coordinates have sent to database");

            }else{
                Log.d("az","location not found");
            }

        }

    }


    private class cGPSListener implements GpsStatus.Listener {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    break;

                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    // mDialog.dismiss();
//                    Log.i(getLocalClassName(), "GPS fix obtained.");
                    break;

                case GpsStatus.GPS_EVENT_STOPPED:
                    break;
            }
        }
    }



}

