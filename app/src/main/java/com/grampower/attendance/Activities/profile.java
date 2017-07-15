package com.grampower.attendance.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.grampower.attendance.Databases.DataBase;
import com.grampower.attendance.Fragments.FullScreenProfileEditor;
import com.grampower.attendance.Others.FieldForce;
import com.grampower.attendance.R;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

    Context context;
    CircleImageView mCircleImageView;
    Button mUpdateProfile;
    TextView mName, mPhone, mPassword;
    ImageButton mEditName, mEditPhone, mEditPassword, mBackBtn;
    private String name, mobile, password;
    ProgressBar mProgressBar;
    private static final int SELECT_PICTURE = 100;
    String email;
    RelativeLayout mLogOut;
    String TAG = "Attendance";
   public  static String fieldname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;
        mBackBtn = (ImageButton) findViewById(R.id.back_profile);
        mLogOut = (RelativeLayout) findViewById(R.id.logout_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar_profile);
        mCircleImageView = (CircleImageView) findViewById(R.id.profilePhoto);
        mUpdateProfile = (Button) findViewById(R.id.editProfile);
        mName = (TextView) findViewById(R.id.nameProfile);
        mPhone = (TextView) findViewById(R.id.mobileNumber);
        mEditName = (ImageButton) findViewById(R.id.editName);
        mEditPhone = (ImageButton) findViewById(R.id.editMobileNumber);

        email = FieldForce.getInstance().getEmail();

        updateUI();

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile.this, MainActivity.class));
                finish();
            }
        });

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("MYPREFERENCES", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(profile.this, LoginActivity.class));
                finish();
            }
        });
        mUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }
        });

        mEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldname="name";
                FullScreenDialogFragment dialogFragment = new FullScreenDialogFragment.Builder(profile.this)
                        .setTitle(email)
                        .setConfirmButton(null)
                        .setOnConfirmListener(null)
                        .setOnDiscardListener(null)
                        .setContent(FullScreenProfileEditor.class, null)
                        .build();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogToolbar);
                dialogFragment.show(getSupportFragmentManager(), "New Name");
            }
        });
        mEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fieldname="mobile";
                FullScreenDialogFragment dialogFragment = new FullScreenDialogFragment.Builder(profile.this)
                        .setTitle(email)
                        .setConfirmButton(null)
                        .setOnConfirmListener(null)
                        .setOnDiscardListener(null)
                        .setContent(FullScreenProfileEditor.class, null)
                        .build();

                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogToolbar);
                dialogFragment.show(getSupportFragmentManager(), "New Name");

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    mCircleImageView.setImageURI(selectedImageUri);
                    mCircleImageView.setDrawingCacheEnabled(true);
                    mCircleImageView.buildDrawingCache();
                    Bitmap bitmap = mCircleImageView.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data2 = baos.toByteArray();
                    String encodedImageString = Base64.encodeToString(data2, Base64.DEFAULT);
                    DataBase dataBase = new DataBase(context);
                    dataBase.updateProfilePic(dataBase, encodedImageString);
                    updateUI();


                }
            }
        }


    }

    void updateUI() {

        mProgressBar.setVisibility(View.VISIBLE);
        DataBase dataBase = new DataBase(profile.this);
        Cursor crsr = dataBase.getProfileData(dataBase);

        if (crsr.moveToFirst()) {

            String name = crsr.getString(1);
            String password = crsr.getString(2);
            String mobilenumber = crsr.getString(4);
            String image_string = crsr.getString(5);
            byte[] image_data = Base64.decode(image_string, Base64.DEFAULT);

            if (image_data != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(image_data, 0, image_data.length);
                if (imageBitmap != null) {
                    mCircleImageView.setImageBitmap(imageBitmap);
                    Log.d(TAG, "image bitmap is not null");
                } else {
                    Log.d(TAG, "image bitmap is null");
                }
            }
            mName.setText(name);
            mPhone.setText(mobilenumber);
        } else {
            Toast.makeText(context, "error in loading", Toast.LENGTH_SHORT);
        }

        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(profile.this, MainActivity.class));
        finish();
    }


   public String getFieldName(){
        return fieldname;
    }
}
