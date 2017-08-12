package com.grampower.fieldforce.Activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.grampower.fieldforce.Databases.DataBase;
import com.grampower.fieldforce.Others.FieldForce;
import com.grampower.fieldforce.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.grampower.fieldforce.Activities.OnlineAttendance.REQUEST_IMAGE_CAPTURE_END_AATENDANCE;
import static com.grampower.fieldforce.Activities.OnlineAttendance.REQUEST_IMAGE_CAPTURE_START_AATENDANCE;

/**
 * Created by sam on 15/7/17.
 */

public class OfflineAttendance extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int LOCATION_REQUEST_CHECK_SETTINGS = 1000;

    CircleImageView mProfileView;
    LinearLayout mBack;
    Context context;
    LocationRequest locationRequest;
    Location mLastLocation;
    ProgressBar mProgressBar;
    ProgressDialog progressDialog;
    CoordinatorLayout mCoordinatorLayout;
    protected GoogleApiClient mGoogleApiClient;
    LocationManager mLocationManager;
    cGPSListener mGPSListner;
    static Boolean locationChangedFlag = false;
    private static final int GPS_RUNTIME_TIMEOUT_MS = 60000;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;
    Boolean isGPSEnabled, isNetworkEnabled;
    AlarmManager alarms;
    PendingIntent pendingIntent;

    PieChart mAttendancePanel;

    public static final int[] Colors = {Color.rgb(148, 49, 38), Color.rgb(241, 155, 44), Color.rgb(255, 88, 48), Color.rgb(228, 126, 48), Color.rgb(183, 149, 11), Color.rgb(169, 50, 38), Color.rgb(2094, 8, 25)};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_attendance);

        Log.d("az", "offline activity");

        context = OfflineAttendance.this;
        mProgressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar_selfie);
        mBack = (LinearLayout) findViewById(R.id.backLayout_selfie);
        mProfileView = (CircleImageView) findViewById(R.id.profile_selfie_toolbar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mAttendancePanel = (PieChart) findViewById(R.id.attendance_panel);

        if (FieldForce.getInstance().getProfileBitmap() != null) {
            mProfileView.setImageBitmap(FieldForce.getInstance().getProfileBitmap());
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });

        setAttendancePanel();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        checkPermissions();
    }


    public LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d("az", "on location changed");
            mLastLocation = location;
            locationChangedFlag = true;
            // getLocation();

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


    void getLocation() {


        if (ActivityCompat.checkSelfPermission(OfflineAttendance.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestLocationPermission();
            return;
        }

        mGPSListner = new cGPSListener();
        mLocationManager.addGpsStatusListener(mGPSListner);


        if (isGPSEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
            if (mLocationManager != null) {
                Location locate = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locate != null) {
                    Log.d("az", "location from gps get location");
                    mLastLocation = locate;
                }
            }
        }


        if (isNetworkEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
            if (mLocationManager != null) {
                Location networkLocate = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (networkLocate != null) {
                    Log.d("az", "location from network get location");
                    mLastLocation = networkLocate;
                }
            }
        }

    }


    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(OfflineAttendance.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                showGPSDialog();
            }
        } else {
            showGPSDialog();
        }

    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(OfflineAttendance.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(OfflineAttendance.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(OfflineAttendance.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (mGoogleApiClient == null) {
                        buildGoogleApiClient();

                        showGPSDialog();
                    } else {
                        showGPSDialog();
                    }
                } else {
                    Toast.makeText(OfflineAttendance.this, "Location Permission denied, App can not get your location", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
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
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("azad", "connected");
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


    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }


    private void recordAttendance(Bitmap imagedata, String attendType) {
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
        String currentLocation = mLastLocation.getLatitude() + "_" + mLastLocation.getLongitude();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagedata.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String encodedSelfieString = Base64.encodeToString(data, Base64.DEFAULT);

        String selfieName = "selfie_" + email + "_" + timestamp + ".jpg";
        DataBase dataBase = new DataBase(context);
        dataBase.insertAttendance(dataBase, todayDate, attendType, currentLocation, currentTime, selfieName, encodedSelfieString, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (attendType.equals("morning")) {
            editor.putString("startAttendance", todayDate);
        } else {
            editor.putString("endAttendance", todayDate);
        }
        editor.commit();


        mProgressBar.setVisibility(View.GONE);
        Toast.makeText(context, "Your presence have recorded", Toast.LENGTH_LONG).show();
    }


    void showGPSDialog() {

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
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        getLocation();
                        Log.d("az", " start waitforlocation from success dialog");
                        new waitForLocation().execute();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        Toast.makeText(context, "Resolution", Toast.LENGTH_SHORT).show();
                        try {

                            status.startResolutionForResult(OfflineAttendance.this, LOCATION_REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Toast.makeText(context, "not satisfied", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE_START_AATENDANCE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    recordAttendance(imageBitmap, "morning");
                }
                break;
            case REQUEST_IMAGE_CAPTURE_END_AATENDANCE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    recordAttendance(imageBitmap, "evening");
                }
                break;

            case LOCATION_REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        getLocation();
                        Log.d("az", " start waitforlocation  pressing onEnable GPS");
                        new waitForLocation().execute();
                        Toast.makeText(context, "enabled the GPS", Toast.LENGTH_LONG).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(context, "You have canceled GPS request, Now you can't record your attendance! ", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }

    }

    class waitForLocation extends AsyncTask<Void, Void, Location> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(OfflineAttendance.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("getting Location....");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.d("az", "in pre execute");
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
                        requestLocationPermission();
                        return null;
                    }

                    if (locationChangedFlag && mLastLocation != null) {
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

            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (location != null) {
                Log.d("az", location.getLatitude() + "  post execute from Offline " + location.getLongitude());

                if (location.getAccuracy() < MIN_DISTANCE_CHANGE_FOR_UPDATES) {

                } else {
                    new SweetAlertDialog(OfflineAttendance.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Location")
                            .setContentText("Location Accuracy is :" + location.getAccuracy() + ", Try for more accuracy!")
                            .setConfirmText("Yes")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    getLocation();
                                    Log.d("az", " start waitforlocation from Alertdialog 1");

                                    new waitForLocation().execute();
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setCancelText("No")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                }
            } else {
                new SweetAlertDialog(OfflineAttendance.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Location")
                        .setContentText("Sorry! Location not found! Are You wanna try again?")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                getLocation();
                                Log.d("az", " start waitforlocation from Alertdialog 2");
                                new waitForLocation().execute();
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setCancelText("No")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                startActivity(new Intent(context,MainActivity.class));
                                sweetAlertDialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }


        }
    }


    private class cGPSListener implements GpsStatus.Listener {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    //showGPSDialog();
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


    void setAttendancePanel() {

        ArrayList<Entry> yValues = new ArrayList<Entry>();

        for (int i = 0; i < 4; i++) {
            yValues.add(new Entry(25, i));
        }
        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("Start Day");
        xValues.add("Start Work");
        xValues.add("End Work");
        xValues.add("End Day");
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(10);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : Colors) {
            colors.add(c);
        }
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        PieData data = new PieData(xValues, dataSet);
        data.setValueTextSize(11f);

        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(15);
        mAttendancePanel.setData(data);
        mAttendancePanel.setDescription(null);
        mAttendancePanel.setUsePercentValues(false);
        mAttendancePanel.setRotationEnabled(false);
        mAttendancePanel.highlightValues(null);
        mAttendancePanel.setRotationAngle(225);
        mAttendancePanel.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/neo_sans_bold.ttf"));
        mAttendancePanel.setDrawSliceText(true);
        //int color= Color.parseColor("#1976d2");
        //  mAttendancePanel.setHoleColor(color);
        mAttendancePanel.setDrawCenterText(true);
        mAttendancePanel.setCenterText("Attendance");
        mAttendancePanel.setCenterTextColor(Color.BLACK);
        mAttendancePanel.setCenterTextSize(20);
        mAttendancePanel.getLegend().setEnabled(false);
        mAttendancePanel.setDrawCenterText(true);
        // refresh/update pie chart
        mAttendancePanel.invalidate();
        // animate piechart
        // mAttendancePanel.animateXY(1400, 1400);

        mAttendancePanel.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                checkAttendanceType(e.getXIndex());
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }


    void checkAttendanceType(int attendanceIndex) {

        long date = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yy");
        String todaydate = dateFormat.format(date);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MYPREFERENCES", Context.MODE_PRIVATE);
        String startDate = sharedPreferences.getString("startAttendance", "");

        switch (attendanceIndex) {

            case 0:
                if (todaydate.equals(startDate)) {
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "You have already recorded morning presence.", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();

                } else {
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

                break;

            case 1:
                startTrackerTimer();
                break;

            case 2:
                stopTrackerTimer();
                break;

            case 3:
                String endDate = sharedPreferences.getString("endAttendance", "");
                if (todaydate.equals(startDate)) {
                    if (todaydate.equals(endDate)) {
                        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "You have already recorded evening presence", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    } else {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_END_AATENDANCE);
                        }
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Record your morning presence first", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }

                break;
        }

    }


    void startTrackerTimer() {

        try {
            Intent intent = new Intent(OfflineAttendance.this, LocationTracker.class);

            pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(),
                    232323232, // id, optional
                    intent, // intent to launch
                    0); // PendintIntent flag

            alarms = (AlarmManager) getSystemService(ALARM_SERVICE);

            //alarms.setRepeating(AlarmManager.RTC_WAKEUP, when.getTime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
            alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 3*60*1000, pendingIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void stopTrackerTimer() {

        try {
            Intent intent = new Intent(OfflineAttendance.this, LocationTracker.class);

            pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(),
                    232323232, // id, optional
                    intent, // intent to launch
                    0); // PendintIntent flag

            alarms = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarms.cancel(pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
