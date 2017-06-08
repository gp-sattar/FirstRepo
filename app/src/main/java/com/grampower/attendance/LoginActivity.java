package com.grampower.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity{


    private TextInputEditText mEmailView;
    private  TextInputEditText mPasswordView;

     TextView mNewAccount,mForgotpassword;
    Context context;
    ProgressBar mProgressBar;
    ImageButton mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=this;

        final FirebaseAuth authen=FirebaseAuth.getInstance();
        mProgressBar=(ProgressBar)findViewById(R.id.progress_bar_login);
        mBack=(ImageButton)findViewById(R.id.back_login);
        mEmailView = (TextInputEditText) findViewById(R.id.loginemail);
        mPasswordView = (TextInputEditText) findViewById(R.id.loginpassword);
        mNewAccount=(TextView)findViewById(R.id.new_account);
        mForgotpassword=(TextView)findViewById(R.id.forgotPassword);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        String htmlString="<u>Forgot Password?</u>";
        mForgotpassword.setText(Html.fromHtml(htmlString));

        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,MainActivity.class));
                finish();
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               String textEmail=mEmailView.getText().toString().trim();
               final String mail=textEmail.split("\\.")[0];
                final String  textPassword=mPasswordView.getText().toString().trim();
                mProgressBar.setVisibility(View.VISIBLE);
                authen.signInWithEmailAndPassword(mail,textPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(context,"You have successfully logged",Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("mail",mail);
                            editor.putString("password",textPassword);
                            editor.commit();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
                            databaseReference.child(mail).child("profile").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String name=dataSnapshot.child("name").getValue().toString();
                                    String proifleUrl=dataSnapshot.child("profileUrl").getValue().toString();
                                    SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("name",name);
                                    editor.putString("profileUrl",proifleUrl);
                                    editor.commit();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }else{
                            Toast.makeText(context,"You haven't logged, Try again",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context,LoginActivity.class));
                        }
                    }
                });
            }
        });

        mNewAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,SignUp.class));

            }
        });
    }



}