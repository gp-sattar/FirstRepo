package com.grampower.attendance.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by samdroid on 5/6/17.
 */

public class DataBase extends SQLiteOpenHelper {

    Context context;
    public static final int database_version = 1;
    public String sql_query_profile="create table profile (email varchar(30), name varchar(30),password varchar(25),gender varchar(6),mobile varchar(10),profile_image_data MEDIUMTEXT,syncStatus int);";
    public String sql_query_task="create table tasks (id integer primary key autoincrement, date varchar(8),taskid varchar(6) not NULL, taskstatement varchar(20), status varchar(20), detail varchar(200),syncStatus int);";
    public String sql_query_attendance="create table attendance (id integer primary key autoincrement, date varchar(8),attendanceType varchar(10),currentLocation varchar(40),currentTime varchar(40),selfie_name varchar(40), selfie_data blob, syncStatus int);";
    public String sql_query_notifications="create table notifications (id integer primary key autoincrement,notify_id varchar(20), notification varchar(300));";
    public static String TAG=DataBase.class.getSimpleName();

    public DataBase(Context context) {
        super(context, "MyDatabase",null, database_version);
        Log.d("database operation", "database is created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_query_profile);
        db.execSQL(sql_query_attendance);
        db.execSQL(sql_query_task);
        db.execSQL(sql_query_notifications);
        Log.d("database operation", "tables have created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    public  void  insertProfile(DataBase ob,String email, String name,String password, String gender , String mobile , String profile_image_data){
        Log.d(TAG," after insert profile");
        SQLiteDatabase sq=ob.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("email",email);
        cv.put("name", name);
        cv.put("password",password);
        cv.put("gender",gender);
        cv.put("mobile",mobile);
        cv.put("profile_image_data",profile_image_data);
        cv.put("syncStatus",1);
        sq.insert("profile", null, cv);
        Log.d(TAG, "profile  is inserted");
    }

    public Cursor getProfileData(DataBase object){
        SQLiteDatabase SQ=object.getReadableDatabase();
        String sql_query="select * from profile";
        Cursor cr = SQ.rawQuery(sql_query, null);
        Log.d(TAG, "profile  have read ");
        return cr;
    }


    public void updateProfilePic(DataBase object,String data) {
        try {
            SQLiteDatabase SQ = object.getWritableDatabase();
            String sql_query = "UPDATE profile SET profile_image_data ='"+data+"', syncStatus=0" ;
            SQ.execSQL(sql_query);
            Log.d(TAG, "profile  have updated ");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void updateProfileFields(DataBase object,String fieldName,String fieldValue){
        try{ SQLiteDatabase SQ=object.getWritableDatabase();
            String sql_query="UPDATE profile SET "+fieldName+"='"+fieldValue+"', syncStatus=0";
            SQ.execSQL(sql_query);
        }catch(SQLiteException e){
            e.printStackTrace();
        }
    }

    public Cursor syncStatusGetter(DataBase object){
        SQLiteDatabase SQ=object.getReadableDatabase();
        String sql_query="select syncStatus from profile";
        Cursor cr = SQ.rawQuery(sql_query, null);
        return cr;
    }

    public void syncStatusSetter(DataBase object,int status){
        try{ SQLiteDatabase SQ=object.getWritableDatabase();
            String sql_query="UPDATE profile SET syncStatus="+status;
            SQ.execSQL(sql_query);
        }catch(SQLiteException e){
            e.printStackTrace();
        }
    }



    public  void  insertAttendance(DataBase ob,String date, String attendanceType,String currentLocation, String currentTime , String selfieName, String selfie_data,int syncStatus){
        SQLiteDatabase sq=ob.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("date",date);
        cv.put("attendanceType", attendanceType);
        cv.put("currentLocation",currentLocation);
        cv.put("currentTime",currentTime);
        cv.put("selfie_name",selfieName);
        cv.put("selfie_data",selfie_data);
        cv.put("syncStatus",syncStatus);
        sq.insert("attendance", null, cv);
        Log.d("database operation", "attendance is inserted");
    }

    public Cursor getAttendance(DataBase object){
        Cursor cr=null;
        try{ SQLiteDatabase SQ=object.getReadableDatabase();
            String sql_query="select * from attendance where syncStatus=0";
            cr = SQ.rawQuery(sql_query, null);
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return cr;
    }

    public void syncStatusAtttendanceSetter(DataBase object){
        try{ SQLiteDatabase SQ=object.getWritableDatabase();
            String sql_query="UPDATE attendance SET syncStatus=1";
            SQ.execSQL(sql_query);
        }catch(SQLiteException e){
            e.printStackTrace();
        }
    }

    public  void  insertTask(DataBase ob,String date, String taskid,String taskstatement, String status,String detail,int syncStatus){
        SQLiteDatabase sq=ob.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("date",date);
        cv.put("taskid",taskid);
        cv.put("taskstatement",taskstatement);
        cv.put("status",status);
        cv.put("detail",detail);
        cv.put("syncStatus",syncStatus);
        sq.insert("tasks", null, cv);
        Log.d("database operation", "task is inserted");
    }


    public Cursor sentReportedTaskToServer(DataBase ob){
        Cursor cr=null;
        try{ SQLiteDatabase SQ=ob.getReadableDatabase();
            String sql_query="select * from tasks where syncStatus=0";
            cr = SQ.rawQuery(sql_query, null);
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return cr;
    }

    public Cursor getTask(DataBase object,String date){
        Cursor cr=null;
        try{ SQLiteDatabase SQ=object.getReadableDatabase();
            String sql_query="select * from tasks where date='"+date+"'";
            cr = SQ.rawQuery(sql_query, null);
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return cr;
    }

    public void insertNotification(DataBase object,String id, String  notification){
        SQLiteDatabase sq=object.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("notify_id",id);
        cv.put("notification",notification);
        sq.insert("notifications", null, cv);
    }

    public Cursor getNotifications(DataBase object){
        Cursor cr=null;
        try{ SQLiteDatabase SQ=object.getReadableDatabase();
            String sql_query="select * from notifications";
            cr = SQ.rawQuery(sql_query, null);
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return  cr;
    }

    public void deleteNotifications(DataBase object){
        SQLiteDatabase sq=object.getWritableDatabase();
        String sql_query="DELETE FROM notifications;";
        sq.execSQL(sql_query);
        Log.d(TAG,"delete from notification");

    }


}
