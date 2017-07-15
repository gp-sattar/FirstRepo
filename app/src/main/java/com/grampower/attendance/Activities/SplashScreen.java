package com.grampower.attendance.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.grampower.attendance.R;

public class SplashScreen extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context=this;
        SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("email")){
            startActivity(new Intent(this, MainActivity.class));
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}
