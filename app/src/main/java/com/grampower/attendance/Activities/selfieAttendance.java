package com.grampower.attendance.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.grampower.attendance.Databases.DataBase;
import com.grampower.attendance.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class selfieAttendance extends AppCompatActivity  implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    CircleImageView mProfileView;
    LinearLayout mBack;
    Context context;
    LocationRequest locationRequest;
    Location mLastLocation;
    ProgressBar mProgressBar;
    static final int REQUEST_IMAGE_CAPTURE_START_AATENDANCE = 1;
    static final int REQUEST_IMAGE_CAPTURE_END_AATENDANCE = 2;
    ProgressDialog progressDialog;
    RelativeLayout mStartAttend,mEndAttend;
    protected GoogleApiClient mGoogleApiClient;
    CoordinatorLayout mCoordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_attendance);


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {

                            status.startResolutionForResult(selfieAttendance.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });


        context = this;
        mProgressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar_selfie);
        mStartAttend=(RelativeLayout)findViewById(R.id.startAttendance);
        mEndAttend=(RelativeLayout)findViewById(R.id.endAttendance);
        mBack=(LinearLayout) findViewById(R.id.backLayout_selfie);
        mProfileView = (CircleImageView) findViewById(R.id.profile_selfie_toolbar);
        mCoordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        DataBase dataBase=new DataBase(context);
        Cursor crsr= dataBase.getProfileData(dataBase);
        if(crsr.moveToFirst()){
            String image_string = crsr.getString(5);
            byte[] image_data = Base64.decode(image_string, Base64.DEFAULT);
            if (image_data != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(image_data, 0, image_data.length);
                    mProfileView.setImageBitmap(imageBitmap);
            }
        }


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,MainActivity.class));
                finish();
            }
        });




      mStartAttend.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              long date = System.currentTimeMillis();
              SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yy");
              String todaydate = dateFormat.format(date);
              SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
              String startDate=sharedPreferences.getString("startAttendance","");

              if(todaydate.equals(startDate)){
                  Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "You have already recorded morning presence.", Snackbar.LENGTH_LONG);
                  View sbView = snackbar.getView();
                  TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                  textView.setTextColor(Color.YELLOW);
                  snackbar.show();

              }else{
                  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                      takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                  } else {
                      takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                  }
                  if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_START_AATENDANCE);
                  }
              }

          }
      });

     mEndAttend.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             long date = System.currentTimeMillis();
             SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yy");
             String todaydate = dateFormat.format(date);
             SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
             String startDate=sharedPreferences.getString("startAttendance","");
             String   endDate=sharedPreferences.getString("endAttendance","");
             if(todaydate.equals(startDate)){
                 if(todaydate.equals(endDate)){
                     Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "You have already recorded evening presence", Snackbar.LENGTH_LONG);
                     View sbView = snackbar.getView();
                     TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                     textView.setTextColor(Color.YELLOW);
                     snackbar.show();
                 }else{
                     Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                     if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                         startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_END_AATENDANCE);
                     }
                 }
             }else{
                 Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Record your morning presence first", Snackbar.LENGTH_LONG);
                 View sbView = snackbar.getView();
                 TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                 textView.setTextColor(Color.YELLOW);
                 snackbar.show();
             }

         }
     });

        new waitForLocation().execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE_START_AATENDANCE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
               recordAttendance(imageBitmap,"morning");
        }else {
            if (requestCode == REQUEST_IMAGE_CAPTURE_END_AATENDANCE && resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                recordAttendance(imageBitmap,"evening");
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("azad", "connected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        createLocationRequest();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("azad", mLastLocation.getLatitude() + " from Connected " + mLastLocation.getLongitude());
        } else {
            //Toast.makeText(this,"location is not find", Toast.LENGTH_LONG).show();
            Log.d("azad", "location not found");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("az", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("az", "Connection failed");
        mGoogleApiClient.connect();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.d("azad", "googleClient has created");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("azad", "location have changed");
        mLastLocation = location;
        Log.d("azad", mLastLocation.getLatitude() + " from locationChanged " + mLastLocation.getLongitude());
    }

    protected void createLocationRequest() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("azad", "Service destroyed!");
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    class waitForLocation extends AsyncTask<Void, Void, Void> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(selfieAttendance.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("getting Location....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            boolean isDataFetched = false;
            while (!isDataFetched) {
                if (mLastLocation != null) {
                    isDataFetched = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(selfieAttendance.this,mLastLocation.getLatitude()+"  "+mLastLocation.getLongitude(),Toast.LENGTH_LONG);
            Log.d("az",mLastLocation.getLatitude()+"  "+mLastLocation.getLongitude());
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
           // new getAddress().execute();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context,MainActivity.class));
        finish();
    }





    private void recordAttendance(Bitmap imagedata,String attendType) {
        mProgressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MYPREFERENCES", Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString("email", "");
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy_hhmmss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yy");
        String timestamp = simpleDateFormat.format(date);
        final String currentTime = timeFormat.format(date);
        final String todayDate = dateFormat.format(date);
       String currentLocation=mLastLocation.getLatitude()+"_"+mLastLocation.getLongitude();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagedata.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String encodedSelfieString = Base64.encodeToString(data, Base64.DEFAULT);

      String selfieName="selfie_" + email + "_" + timestamp + ".jpg";
        DataBase dataBase=new DataBase(context);
        dataBase.insertAttendance(dataBase,todayDate,attendType,currentLocation,currentTime,selfieName,encodedSelfieString,0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(attendType.equals("morning")){
            editor.putString("startAttendance",todayDate);
        }else{
            editor.putString("endAttendance",todayDate);
        }
        editor.commit();

        mProgressBar.setVisibility(View.GONE);
        Toast.makeText(context, "Your presence have recorded", Toast.LENGTH_LONG).show();
    }

}
