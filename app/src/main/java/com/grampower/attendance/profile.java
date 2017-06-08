package com.grampower.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

    Context context;
    CircleImageView  mCircleImageView;
    Button mUpdateProfile;
    TextView mName,mPhone,mPassword;
    ImageButton mEditName,mEditPhone,mEditPassword,mBackBtn;
    private String name,mobile,password;
    ProgressBar mProgressBar;
    private static final int SELECT_PICTURE = 100;
    String email;
    PopupWindow popup;
    RelativeLayout mLogOut;
    String TAG="Attendance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context=this;
        mBackBtn=(ImageButton)findViewById(R.id.back_profile);
        mLogOut=(RelativeLayout)findViewById(R.id.logout_layout);
        mProgressBar=(ProgressBar)findViewById(R.id.toolbar_progress_bar_profile);
        mCircleImageView=(CircleImageView)findViewById(R.id.profilePhoto);
        mUpdateProfile=(Button)findViewById(R.id.editProfile);
        mName=(TextView)findViewById(R.id.nameProfile);
        mPhone=(TextView)findViewById(R.id.mobileNumber);
        mPassword=(TextView)findViewById(R.id.passwordProfile);
        mEditName=(ImageButton)findViewById(R.id.editName);
        mEditPhone=(ImageButton)findViewById(R.id.editMobileNumber);
        mEditPassword=(ImageButton)findViewById(R.id.editPassword);
        SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
        email=sharedPreferences.getString("email","");


            updateUI();


       /*  DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
       databaseReference.child(email).child("profile").addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                    getInfo(dataSnapshot);
                  mProgressBar.setVisibility(View.GONE);
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });*/



        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile.this,MainActivity.class));
                finish();
            }
        });

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(profile.this,LoginActivity.class));
                finish();
            }
        });
         mUpdateProfile.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i = new Intent();
                 i.setType("image/*");
                 i.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(Intent.createChooser(i, "Select Picture"),SELECT_PICTURE );
             }
         });

        mEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow(email,"name",name);
            }
        });
        mEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow(email,"mobile",mobile);
            }
        });
        mEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow(email,"password",password);
            }
        });
    }

    void getInfo(DataSnapshot data){
         name=data.child("name").getValue().toString();
         mobile=data.child("mobile").getValue().toString();
         password=data.child("password").getValue().toString();
        String  profileUrl=data.child("profileUrl").getValue().toString();
        Picasso.with(this).load(profileUrl).into(mCircleImageView);
        mName.setText(name);
        mPhone.setText(mobile);
        mPassword.setText(password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    mCircleImageView.setImageURI(selectedImageUri);
                    //StorageReference storageRef= FirebaseStorage.getInstance().getReferenceFromUrl("gs://attendacemodule.appspot.com/");
                    //StorageReference myfileRef = storageRef.child("profilePics/"+email+".jpg");
                    mCircleImageView.setDrawingCacheEnabled(true);
                    mCircleImageView.buildDrawingCache();
                    Bitmap bitmap = mCircleImageView.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data2 = baos.toByteArray();
                    String encodedImageString = Base64.encodeToString(data2, Base64.DEFAULT);

                    DataBase dataBase=new DataBase(context);
                    dataBase.updateProfilePic(dataBase,encodedImageString);
                    updateUI();
                   /* UploadTask uploadTask = myfileRef.putBytes(data2);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(context, "Not Update ,Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            DatabaseReference userDatabase= FirebaseDatabase.getInstance().getReference("users");
                            userDatabase.child(email).child("profile").child("profileUrl").setValue(downloadUrl.toString());
                            SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("profileUrl",downloadUrl.toString());
                            editor.commit();
                            Toast.makeText(context, "Your Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
*/


                }
            }
        }


    }

     void popupWindow(String usermail, final String fieldName, final String fieldValue){

         LayoutInflater inflater = (LayoutInflater) profile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View layout = inflater.inflate(R.layout.popupwindow, (ViewGroup) findViewById(R.id.popuplayout));
         LinearLayout linearLayout=(LinearLayout)layout.findViewById(R.id.popuplayout);
         popup = new PopupWindow(layout,700, 380, true);
         popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
         TextView emailHeader=(TextView)layout.findViewById(R.id.emailPopup);
         ImageButton close=(ImageButton)layout.findViewById(R.id.close);
         final EditText updateField=(EditText)layout.findViewById(R.id.editTextPopup);
         emailHeader.setText(usermail);
         updateField.setHint("enter new "+fieldName);
         Button btnUpdateField = (Button)layout.findViewById(R.id.updatePopup);
         Button btnClosePopup = (Button)layout.findViewById(R.id.closePopup);
         btnClosePopup.setOnClickListener(new Button.OnClickListener() {
             @Override
             public void onClick(View v) {
                 popup.dismiss();
             }
         });
         close.setOnClickListener(new Button.OnClickListener() {
             @Override
             public void onClick(View v) {
                 popup.dismiss();
             }
         });
         btnUpdateField.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  updatedValue=updateField.getText().toString().trim();
                DataBase dataBase=new DataBase(context);
                dataBase.updateProfileFields(dataBase,fieldName,updatedValue);
                updateUI();
                popup.dismiss();
                Toast.makeText(context, "Your "+fieldName+" have updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

     }

   void updateUI(){

       mProgressBar.setVisibility(View.VISIBLE);
         DataBase dataBase=new DataBase(profile.this);
       Cursor crsr=dataBase.getProfileData(dataBase);

       if(crsr.moveToFirst()) {

           String name = crsr.getString(1);
           String password = crsr.getString(2);
           String mobilenumber = crsr.getString(4);
           String image_string = crsr.getString(5);
           byte[] image_data = Base64.decode(image_string, Base64.DEFAULT);

           if (image_data != null) {
               Bitmap imageBitmap = BitmapFactory.decodeByteArray(image_data, 0, image_data.length);
               if(imageBitmap!=null) {
                   mCircleImageView.setImageBitmap(imageBitmap);
                   Log.d(TAG, "image bitmap is not null");
               }else{
                   Log.d(TAG, "image bitmap is null");
               }
           }
           mName.setText(name);
           mPassword.setText(password);
           mPhone.setText(mobilenumber);
       }else{
           Toast.makeText(context,"error in loading",Toast.LENGTH_SHORT);
       }

       mProgressBar.setVisibility(View.GONE);
   }


}
