package com.grampower.fieldforce.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developers.coolprogressviews.CircleWithArcProgress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grampower.fieldforce.Databases.DataBase;
import com.grampower.fieldforce.Others.FieldForce;
import com.grampower.fieldforce.Others.LinearLayoutThatDetectsSoftKeyboard;
import com.grampower.fieldforce.Others.Utils;
import com.grampower.fieldforce.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends AppCompatActivity {

    private EditText mEmailView, mPasswordView;
    ProgressDialog progressDialog;
    TextView mNewAccount, mForgotpassword;
    Context context;
    Button mEmailSignInButton;
    ProgressBar mProgressBar;
    CoordinatorLayout mSignInLayout;
    LinearLayout mCentrallayout;
    FrameLayout mVisibilityLayout;
    ImageView mVisibilityEye;
    CircleWithArcProgress mCircleWithArcProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        mCircleWithArcProgress = (CircleWithArcProgress) findViewById(R.id.cp_sign_in);
        mVisibilityEye = (ImageView) findViewById(R.id.iv_visibility_eye);
        mVisibilityLayout = (FrameLayout) findViewById(R.id.fl_visibility);
        mSignInLayout = (CoordinatorLayout) findViewById(R.id.cl_sign_in);
        mCentrallayout = (LinearLayout) findViewById(R.id.ll_central_layout);
        //  mProgressBar=(ProgressBar)findViewById(R.id.progress_bar_login);
        mEmailView = (EditText) findViewById(R.id.et_mobile_number);
        mPasswordView = (EditText) findViewById(R.id.et_password);

        mNewAccount = (TextView) findViewById(R.id.tv_sign_up);
        // mForgotpassword=(TextView)findViewById(R.id.forgotPassword);
        mEmailSignInButton = (Button) findViewById(R.id.bt_sign_in);

        final LinearLayoutThatDetectsSoftKeyboard listen = new LinearLayoutThatDetectsSoftKeyboard(mCentrallayout);
        mSignInLayout.getViewTreeObserver().addOnGlobalLayoutListener(listen);

        //mCircleWithArcProgress.set


        mVisibilityLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVisibilityEye.getTag().equals("visible")) {
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPasswordView.setSelection(mPasswordView.length());
                    mVisibilityEye.setImageResource(R.drawable.ic_visibility_off_white_20dp);
                    mVisibilityEye.setTag("invisible");

                } else {
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordView.setSelection(mPasswordView.length());
                    mVisibilityEye.setImageResource(R.drawable.ic_visibility_white_20dp);
                    mVisibilityEye.setTag("visible");
                }
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = mEmailView.getText().toString().trim();
                final String mail = textEmail.split("\\.")[0];
                final String textPassword = mPasswordView.getText().toString().trim();

                if (isNetworkAvailable()) {
                    loginToServer(mail, textEmail, textPassword);
                } else {
                    new SweetAlertDialog(context)
                            .setTitleText("Internet Connection !")
                            .setContentText("Internet Connection is required for Log In ")
                            .show();
                }

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });

        mNewAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SignUp.class));

            }
        });

    }


    void loginToServer(final String mail, String textEmail, final String textPassword) {

        if (textEmail.length() != 0 && !textEmail.equals("") && Utils.isEmailValid(textEmail)) {
            if (textPassword.length() != 0 && !textPassword.equals("")) {

                //mProgressBar.setVisibility(View.GONE);
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading profile...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                databaseReference.child(mail).child("profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        String proifleUrl = dataSnapshot.child("profileUrl").getValue().toString();
                        final String password = dataSnapshot.child("password").getValue().toString();
                        final String gender = dataSnapshot.child("gender").getValue().toString();
                        final String mobile = dataSnapshot.child("mobile").getValue().toString();
                        new AsyncImageloader(mail, name, password, gender, mobile, proifleUrl).execute();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }
                });

            } else {

                mPasswordView.setError("Please Enter Password");
            }
        } else {
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
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    class AsyncImageloader extends AsyncTask<Void, Void, Bitmap> {

        String mail, name, password, gender, mobile, url;


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
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bm = BitmapFactory.decodeStream(bis, null, options);
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
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytedata = stream.toByteArray();
            String encodedImageString = Base64.encodeToString(bytedata, Base64.DEFAULT);

            DataBase dataBase = new DataBase(LoginActivity.this);
            dataBase.insertProfile(dataBase, mail, name, password, gender, mobile, encodedImageString);

            FieldForce.getInstance().setAccountDetails(mail, password, name, url);

            Toast.makeText(context, "You have successfully logged In.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }


}