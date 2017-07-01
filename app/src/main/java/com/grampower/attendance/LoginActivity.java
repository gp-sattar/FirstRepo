package com.grampower.attendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends AppCompatActivity{


    private TextInputEditText mEmailView;
    private  TextInputEditText mPasswordView;
    ProgressDialog progressDialog;
     TextView mNewAccount,mForgotpassword;
    Context context;
    ProgressBar mProgressBar;
    Target target;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=this;
        mProgressBar=(ProgressBar)findViewById(R.id.progress_bar_login);
        mEmailView = (TextInputEditText) findViewById(R.id.loginemail);
        mPasswordView = (TextInputEditText) findViewById(R.id.loginpassword);
        mNewAccount=(TextView)findViewById(R.id.new_account);
        mForgotpassword=(TextView)findViewById(R.id.forgotPassword);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        String htmlString="<u>Forgot Password?</u>";
        mForgotpassword.setText(Html.fromHtml(htmlString));

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = mEmailView.getText().toString().trim();
                final String mail = textEmail.split("\\.")[0];
                final String textPassword = mPasswordView.getText().toString().trim();

                if(isNetworkAvailable()){
                  loginToServer(mail,textEmail,textPassword);
                }else{
                    new SweetAlertDialog(context)
                            .setTitleText("Internet Connection !")
                            .setContentText("Internet Connection is required for Log In ")
                            .show();
                }
            }
            });

        mNewAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,SignUp.class));

            }
        });

    }



    void loginToServer(final String mail,String textEmail,final String textPassword){

        if(textEmail.length()!=0&&!textEmail.equals("")&&Utils.isEmailValid(textEmail)){
            if(textPassword.length()!=0&&!textPassword.equals("")){

                mProgressBar.setVisibility(View.GONE);
                progressDialog=new ProgressDialog(context);
                progressDialog.setMessage("Loading profile...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

              /*  FirebaseAuth authen=FirebaseAuth.getInstance();
                authen.signInWithEmailAndPassword(mail,textPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if(task.isSuccessful()){

                        }else{
                            new SweetAlertDialog(context).setTitleText("Something wrong with Server, Try Again !").show();
                            startActivity(getIntent());
                        }
                    }
                });*/

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
                databaseReference.child(mail).child("profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name=dataSnapshot.child("name").getValue().toString();
                        String proifleUrl=dataSnapshot.child("profileUrl").getValue().toString();
                        final  String password=dataSnapshot.child("password").getValue().toString();
                        final  String gender=dataSnapshot.child("gender").getValue().toString();
                        final String mobile=dataSnapshot.child("mobile").getValue().toString();
                        new AsyncImageloader(mail,name,password,gender,mobile,proifleUrl).execute();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if(progressDialog!=null){
                            progressDialog.dismiss();
                        }

                    }
                });

            }else{

                mPasswordView.setError("Please Enter Password");
            }
        }else{
            mEmailView.setError("Please Enter Valid Email");
        }
    }



    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    class AsyncImageloader extends AsyncTask<Void, Void, Bitmap>{

        String mail,name,password,gender,mobile,url;



        public AsyncImageloader(String mail, String name, String password, String gender, String mobile, String url) {
            this.mail = mail;
            this.name = name;
            this.password = password;
            this.gender = gender;
            this.mobile = mobile;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.setUseCaches(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bm;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(progressDialog!=null){
                progressDialog.dismiss();
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytedata = stream.toByteArray();
            String encodedImageString = Base64.encodeToString(bytedata, Base64.DEFAULT);
            DataBase dataBase=new DataBase(LoginActivity.this);
            dataBase.insertProfile(dataBase,mail,name,password,gender,mobile,encodedImageString);
            SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("email",mail);
            editor.putString("password",password);
            editor.putString("date","welcome");
            editor.putString("startAttendance","noAttendance");
            editor.putString("endAttendance","noAttendance");
            editor.putString("taskSync","newuser");
            editor.putString("name",name);
            editor.putString("profileUrl",url);
            editor.commit();
            Toast.makeText(context, "You have successfully logged In.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }




}