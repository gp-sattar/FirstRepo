package com.grampower.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.grampower.attendance.R.id.register;

public class SignUp extends AppCompatActivity {

    TextInputEditText  mMobile, mName,mEmail,mPassword;
    Button mRegister;
    ImageButton mBack;
    ProgressBar mProgressBar;
    String mail;
    Context context;
    RadioGroup radioGroup;
    RadioButton gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     context=this;
        final String TAG="Attendance";

        final FirebaseAuth authen=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_sign_up);
          mBack=(ImageButton)findViewById(R.id.back_Register);
         mMobile=(TextInputEditText)findViewById(R.id.mono);
         mName=(TextInputEditText)findViewById(R.id.name);
         mEmail=(TextInputEditText)findViewById(R.id.email);
          mPassword=(TextInputEditText)findViewById(R.id.password);
          mRegister=(Button)findViewById(register);
          mProgressBar=(ProgressBar)findViewById(R.id.toolbar_progress_bar_signup);
          radioGroup=(RadioGroup)findViewById(R.id.gender);

           mBack.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   startActivity(new Intent(SignUp.this,MainActivity.class));
               }
           });
          mRegister.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           int selectedId = radioGroup.getCheckedRadioButtonId();
           gender = (RadioButton)findViewById(selectedId);
           final String mGender=gender.getText().toString();
           String textMobile=mMobile.getText().toString().trim();
           String textName=mName.getText().toString().trim();
           String textEmail=mEmail.getText().toString().trim();
           mail=textEmail.split("\\.")[0];
           final String textPassword=mPassword.getText().toString().trim();
           long date= System.currentTimeMillis();
           SimpleDateFormat dateFormat=new SimpleDateFormat("dd_MM_yy");
           final String todayDate=dateFormat.format(date);
          final user newUser=new user(textMobile,textEmail,textName,textPassword,mGender);
           mProgressBar.setVisibility(View.VISIBLE);
          authen.createUserWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {

                  if(task.isSuccessful()){
                     addNewuser(newUser,todayDate);

                        SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("email",mail);
                        editor.putString("password",textPassword);
                        editor.putString("date","welcome");
                        editor.putString("profileUrl",newUser.getProfileUrl());
                        editor.putString("name",newUser.getName());
                        editor.putString("startAttendance","noAttendance");
                        editor.putString("endAttendance","noAttendance");
                        editor.commit();
                      byte[] profile_bytes=selectProfile(newUser.getGender());
                      String encodedImageString = Base64.encodeToString(profile_bytes, Base64.DEFAULT);

                      DataBase dataBase=new DataBase(SignUp.this);
                      dataBase.insertProfile(dataBase,mail,newUser.getName(),newUser.getPassword(),newUser.getGender(),newUser.getMobile(),encodedImageString);
                        Log.d(TAG," after insert profile");
                      mProgressBar.setVisibility(View.GONE);
                      Toast.makeText(getApplicationContext(),"You have registered successfully",Toast.LENGTH_SHORT).show();
                      startActivity(new Intent(SignUp.this,MainActivity.class));
                      finish();

                  }else{
                      Toast.makeText(getApplicationContext(),"Try Again!!",Toast.LENGTH_LONG).show();
                  }
              }
          });

       }
   });



    }

    void addNewuser(user newUser, String date){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        Map<String,Object>  temp=new HashMap<>();
        temp.put(mail,"profile");
        temp.put(mail,"attendance");
        temp.put(mail,"tasks");
        databaseReference.updateChildren(temp);
        Map<String,Object> attendance,tasks;
        attendance=new HashMap<>();
        attendance.put(date,"Welcome to GramPower");
        tasks=new HashMap<>();
        tasks.put(date,"Your First task to rewind your college days");
        databaseReference.child(mail).child("profile").setValue(newUser);
        databaseReference.child(mail).child("attendance").updateChildren(attendance);
        databaseReference.child(mail).child("tasks").updateChildren(tasks);
    }
    byte[] selectProfile(String gender){
        Drawable d;
        if(gender.equals("Male")){
            d=ContextCompat.getDrawable(SignUp.this, R.drawable.male_icon);
        }else{
            d=ContextCompat.getDrawable(SignUp.this, R.drawable.female_icon);
        }
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();

        return  bitmapdata;
    }
}


