package com.grampower.fieldforce.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.grampower.fieldforce.R;

public class RollSelector extends AppCompatActivity {

    TextView mSignIn;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_selector);
         context=this;
        mSignIn=(TextView)findViewById(R.id.tv_sign_in);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,LoginActivity.class));
            }
        });
    }
}
