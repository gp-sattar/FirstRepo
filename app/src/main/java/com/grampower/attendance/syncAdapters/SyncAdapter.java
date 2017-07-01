package com.grampower.attendance.syncAdapters;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grampower.attendance.AttendaceData;
import com.grampower.attendance.DataBase;
import com.grampower.attendance.Task;
import com.grampower.attendance.user;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samdroid on 5/6/17.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {


    public static String TAG = SyncAdapter.class.getSimpleName();
    ContentResolver mContentResolver;
    private AccountManager mAccountManager = null;
    Context context;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
        mAccountManager = AccountManager.get(context);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("az", "ONPerformSync");
        if (isNetworkAvailable()) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MYPREFERENCES", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            FirebaseApp.initializeApp(getContext());
            syncNotifications();
            profileSync();
            attendanceSync();
            newTodayTasksSync(email);
            taskReportSync(email);

        } else {
            Log.d("az", "no network available");
        }

    }

    void profileSync() {

        DataBase dataBase = new DataBase(getContext());

        Cursor cr = dataBase.syncStatusGetter(dataBase);
        if (cr.moveToFirst()) {
            if (cr.getInt(0) == 0) {
                syncStart();
            } else {
                Log.d(TAG, "already updated");
            }
        }
    }


    void syncStart() {
        DataBase dataBase = new DataBase(getContext());
        Cursor crsr = dataBase.getProfileData(dataBase);
        String email = "", name = "", password = "", gender = "", mobile = "";
        byte[] image_data = null;
        if (crsr.moveToFirst()) {
            email = crsr.getString(0);
            name = crsr.getString(1);
            password = crsr.getString(2);
            gender = crsr.getString(3);
            mobile = crsr.getString(4);
            image_data = crsr.getBlob(5);
        }
        uploadProfiliPic(image_data, email, name, password, gender, mobile);
    }


    void uploadProfiliPic(byte[] data, final String email, final String name, final String password, final String gender, final String mobile) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://attendacemodule.appspot.com/");
        StorageReference myfileRef = storageReference.child("profilePics/" + email + ".jpg");
        UploadTask uploadTask = myfileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Not Update ,Try Again", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                syncServerProfileDatabase(email, name, password, gender, mobile, downloadUrl.toString());
            }
        });

    }

    void syncServerProfileDatabase(String email, String name, String password, String gender, String mobile, String url) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users");
        user newUser = new user(mobile, email, name, password, gender);
        newUser.setProfileUrl(url);
        userDatabase.child(email).child("profile").setValue(newUser);
        DataBase dataBase = new DataBase(getContext());
        dataBase.syncStatusSetter(dataBase, 1);
    }


    void attendanceSync() {
        DataBase dataBase = new DataBase(getContext());
        Cursor cr = dataBase.getAttendance(dataBase);
        if (cr.moveToFirst()) {
            do {
                String date = cr.getString(1);
                String typeAttendace = cr.getString(2);
                String currentlocation = cr.getString(3);
                String currentTime = cr.getString(4);
                String selfiename = cr.getString(5);
                String selfiedata = cr.getString(6);
                uploadAttendanceSelfie(date, typeAttendace, currentlocation, currentTime, selfiename, selfiedata);
            } while (cr.moveToNext());

        } else {
            Log.d(TAG, "already updated");
        }
    }

    void uploadAttendanceSelfie(final String date, final String typeAttendance, final String location, final String curTime, String selfiname, String selfieData) {
        byte[] image_data = Base64.decode(selfieData, Base64.DEFAULT);
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://attendacemodule.appspot.com/");
        StorageReference dataRef = storageRef.child(selfiname);
        UploadTask uploadTask = dataRef.putBytes(image_data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Attendance", "error in syncing");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                syncServerAttendanceDatabase(date, typeAttendance, location, curTime, downloadUrl.toString());
            }
        });
    }

    void syncServerAttendanceDatabase(String date, String typeAttendance, String location, String curTime, String selfieUrl) {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MYPREFERENCES", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        AttendaceData todayAttend = new AttendaceData(curTime, selfieUrl, location);
        if (typeAttendance.equals("morning")) {
            Map<String, Object> attedanceMap = new HashMap<>();
            attedanceMap.put(date, "startAttendance");
            databaseReference.child(email).child("attendance").updateChildren(attedanceMap);
            databaseReference.child(email).child("attendance").child(date).child("startAttendance").setValue(todayAttend);
        } else {
            Map<String, Object> attedanceMap = new HashMap<>();
            attedanceMap.put("endAttendance", todayAttend);
            databaseReference.child(email).child("attendance").child(date).updateChildren(attedanceMap);
        }
        DataBase dataBase = new DataBase(getContext());
        dataBase.syncStatusAtttendanceSetter(dataBase);
    }

    void newTodayTasksSync(String email) {

        long date = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yy");
        final String dateText = dateFormat.format(date);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MYPREFERENCES", Context.MODE_PRIVATE);
         String taskSyncDate=sharedPreferences.getString("taskSync","");
         if(!taskSyncDate.equals(dateText)) {

             DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
             databaseReference.child(email).child("tasks").child(dateText).addChildEventListener(new ChildEventListener() {
                 @Override
                 public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                     String taskStatement = dataSnapshot.child("taskstatement").getValue().toString();
                     String taskId = dataSnapshot.child("taskId").getValue().toString();
                     String status = dataSnapshot.child("status").getValue().toString();
                     String detail = dataSnapshot.child("detail").getValue().toString();
                     DataBase dataBase = new DataBase(getContext());
                     dataBase.insertTask(dataBase, dateText, taskId, taskStatement, status, detail, 1);
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

             SharedPreferences.Editor editor=sharedPreferences.edit();
             editor.putString("taskSync",dateText);
             editor.commit();
         }
    }

    void taskReportSync(String email) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        DataBase dataBase = new DataBase(getContext());
        Cursor crsr = dataBase.sentReportedTaskToServer(dataBase);
        if (crsr.moveToFirst()) {
            do {
                String date = crsr.getString(1);
                String taskid = crsr.getString(2);
                String taskstatement = crsr.getString(3);
                String status = crsr.getString(4);
                String detail = crsr.getString(5);
                Task updatedTask = new Task(taskid, taskstatement, status, detail);
                databaseReference.child(email).child("tasks").child(date).child(taskid).setValue(updatedTask);
            } while (crsr.moveToNext());
        }
    }


    void syncNotifications() {

        final DataBase ob = new DataBase(getContext());
        ob.deleteNotifications(ob);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notifications");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                String notifyLine = dataSnapshot.getValue().toString();
                ob.insertNotification(ob, key, notifyLine);
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
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
