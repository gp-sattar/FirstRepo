package com.grampower.fieldforce.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grampower.fieldforce.R;
import com.grampower.fieldforce.pojos.user;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.grampower.fieldforce.R.id.register;

public class SignUp extends AppCompatActivity {

    TextInputEditText mMobile, mName, mEmail, mPassword, mConfirmPassword;
    Button mRegister;
    ImageButton mBack;
    ProgressBar mProgressBar;
    String mail;
    Context context;
    RadioGroup radioGroup;
    RadioButton gender;
    final String TAG = "Attendance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_sign_up);
        mMobile = (TextInputEditText) findViewById(R.id.mono);
        mName = (TextInputEditText) findViewById(R.id.name);
        mEmail = (TextInputEditText) findViewById(R.id.email);
       // mPassword = (TextInputEditText) findViewById(R.id.password);
       // mConfirmPassword = (TextInputEditText) findViewById(R.id.confirmpassword);
        mRegister = (Button) findViewById(register);
        mProgressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar_signup);
        radioGroup = (RadioGroup) findViewById(R.id.gender);


       /* mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                gender = (RadioButton) findViewById(selectedId);
                final String mGender = gender.getText().toString();
                String textMobile = mMobile.getText().toString().trim();
                String textName = mName.getText().toString().trim();
                String textEmail = mEmail.getText().toString().trim();
                mail = textEmail.split("\\.")[0];
                String textPassword = mPassword.getText().toString().trim();


                if (isNetworkAvailable()) {
                    //validateInfo(textName, textEmail, textMobile, textPassword, textConfirmPassword, mGender);
                } else {
                    new SweetAlertDialog(context)
                            .setTitleText("Internet Connection")
                            .setContentText("Internet Connection is required for Sign Up")
                            .show();
                }

            }
        });

*/
    }

/*

    void validateInfo(String textName, String textEmail, String textMobile, String textPassword, String textConfirmPassword, String mGender) {
        if (textMobile.length() != 0 && !textMobile.equals("") && textMobile.length() == 10 && Utils.isMobileNumberValid(textMobile)) {

            if (textEmail.length() != 0 && !textEmail.equals("") && Utils.isEmailValid(textEmail)) {

                if (textName.length() != 0 && !textName.equals("") && Utils.isNameValid(textName)) {

                    if (textPassword.length() != 0 && !textPassword.equals("")) {

                        if (textConfirmPassword.length() != 0 && !textConfirmPassword.equals("")) {


                            if (textPassword.equals(textConfirmPassword)) {

                                createAuthentication(textName, textEmail, textMobile, textPassword, mGender);

                            } else {
                                mConfirmPassword.setError("Please enter the same password again");
                            }

                        } else {
                            mConfirmPassword.setError("Please enter the password to confirm");
                        }
                    } else {
                        mPassword.setError("Please enter the password");
                    }

                } else {
                    mName.setError("Please enter Valid Name");
                }
            } else {
                mEmail.setError("Please enter Valid Email Address");
            }
        } else {
            mMobile.setError("Please enter Valid Mobile Number");
        }


    }
*/

    void createAuthentication(String textName, String textEmail, String textMobile, final String textPassword, String mGender) {

        final FirebaseAuth authen = FirebaseAuth.getInstance();
        long date = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yy");
        final String todayDate = dateFormat.format(date);
        final user newUser = new user(textMobile, textEmail, textName, textPassword, mGender);
        mProgressBar.setVisibility(View.VISIBLE);
        authen.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    addNewuser(newUser, todayDate);
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "You have registered successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUp.this, LoginActivity.class));
                    finish();

                } else {
                    new SweetAlertDialog(context).setTitleText("Oops!").setContentText("Something wrong with server , Try Agin!").show();
                    //Toast.makeText(getApplicationContext(),"Try Again!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    void addNewuser(user newUser, String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Map<String, Object> temp = new HashMap<>();
        temp.put(mail, "profile");
        temp.put(mail, "attendance");
        temp.put(mail, "tasks");
        databaseReference.updateChildren(temp);
        Map<String, Object> attendance, tasks;
        attendance = new HashMap<>();
        attendance.put(date, "Welcome to GramPower");
        tasks = new HashMap<>();
        tasks.put(date, "Your First task to rewind your college days");
        databaseReference.child(mail).child("profile").setValue(newUser);
        databaseReference.child(mail).child("attendance").updateChildren(attendance);
        databaseReference.child(mail).child("tasks").updateChildren(tasks);
    }

 /*   byte[] selectProfile(String gender){
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
    }*/


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


