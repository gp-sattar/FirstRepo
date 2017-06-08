package com.grampower.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AssignTask extends AppCompatActivity {

    Context context;
    ImageButton mBack;
    ProgressBar mProgressBar;
    TextView mEmployee;
    EditText mTaskStatement;
    Button mAssignTask,mNewTask;
    CircleImageView mCircleImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);
        context=this;
        Intent intent=getIntent();
       final String email=intent.getExtras().getString("email");
        String name=intent.getExtras().getString("name");
        mCircleImageView=(CircleImageView)findViewById(R.id.profile_assign_task);
        mTaskStatement=(EditText)findViewById(R.id.taskStatement);
        mProgressBar=(ProgressBar)findViewById(R.id.progressbar_assign_task);
        mBack=(ImageButton)findViewById(R.id.backAssignTask);
        mEmployee=(TextView)findViewById(R.id.employeeName);
        mAssignTask=(Button)findViewById(R.id.assignThisTask);
        mNewTask=(Button)findViewById(R.id.oneMoretask);
        mEmployee.setText(name);
        DataBase dataBase=new DataBase(context);
        Cursor crsr=dataBase.getProfileData(dataBase);
         if(crsr.moveToFirst()){
             String imageString=crsr.getString(5);
             byte[] imagebytes= Base64.decode(imageString,Base64.DEFAULT);
             Bitmap imageBitmap = BitmapFactory.decodeByteArray(imagebytes, 0, imagebytes.length);
             mCircleImageView.setImageBitmap(imageBitmap);
         }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AssignTask.this,employeeListing.class));
                finish();
            }
        });

        mNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getIntent());
                finish();
            }
        });

        mAssignTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task=mTaskStatement.getText().toString().trim();
                sendTaskToServer(task,email);
                mTaskStatement.getText().clear();

            }
        });

    }

    void sendTaskToServer(String powerTask,String email){
        long date=System.currentTimeMillis();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd_MM_yy");
        SimpleDateFormat timeFormat=new SimpleDateFormat("HHmmss");
        String dateText=dateFormat.format(date);
        String timeText=timeFormat.format(date);
        Task todayTask = new Task(timeText,powerTask,"Status not updated yet","No detail till now");

        SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
        String savedDate=sharedPreferences.getString("date","");

        mProgressBar.setVisibility(View.VISIBLE);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        if(savedDate.equals(dateText)){
            Map<String,Object> taskMap=new HashMap<>();
            taskMap.put(timeText,todayTask);
            databaseReference.child(email).child("tasks").child(dateText).updateChildren(taskMap);
        }else{
            Map<String,Object> taskMap=new HashMap<>();
            taskMap.put(dateText,timeText);
            databaseReference.child(email).child("tasks").updateChildren(taskMap);
            databaseReference.child(email).child("tasks").child(dateText).child(timeText).setValue(todayTask);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("date",dateText);
            editor.commit();
        }

        DataBase dataBase=new DataBase(context);
        dataBase.insertTask(dataBase,dateText,timeText,powerTask,todayTask.getStatus(),todayTask.getDetail(),1);

        mProgressBar.setVisibility(View.GONE);
        Toast.makeText(AssignTask.this,"Task have submitted ",Toast.LENGTH_LONG).show();

    }

}
