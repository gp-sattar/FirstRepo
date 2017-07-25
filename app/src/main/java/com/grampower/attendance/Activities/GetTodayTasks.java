package com.grampower.attendance.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grampower.attendance.Databases.DataBase;
import com.grampower.attendance.Fragments.taskFragment;
import com.grampower.attendance.R;
import com.grampower.attendance.pojos.Task;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GetTodayTasks extends AppCompatActivity {

    int taskListSize;
    int currentTask;

    Context context;
    TextView mTaskTurn;
    FragmentManager FragManager;
    RelativeLayout mTopBar;
    List<Task> listOfTodayTask;
    ImageButton mPredecesoor, mSuccessor, mBack;
    CircleImageView mProfile;
    LinearLayout mBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_today_tasks);
        context = this;

        Intent intent = getIntent();
        String date = intent.getExtras().getString("date");

        mProfile = (CircleImageView) findViewById(R.id.profileGetTasks);
      //  mTopBar = (RelativeLayout) findViewById(R.id.topBar);
        mPredecesoor = (ImageButton) findViewById(R.id.predecessor);
        mSuccessor = (ImageButton) findViewById(R.id.successor);
        //mTaskTurn = (TextView) findViewById(R.id.taskTurn);
        mBackLayout = (LinearLayout) findViewById(R.id.back_layout_tasks);
        FragManager = getFragmentManager();

        DataBase dataBase2 = new DataBase(context);
        Cursor crsr2 = dataBase2.getProfileData(dataBase2);
        if (crsr2.moveToFirst()) {
            String image_string = crsr2.getString(5);
            byte[] image_data = Base64.decode(image_string, Base64.DEFAULT);
            if (image_data != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(image_data, 0, image_data.length);
                mProfile.setImageBitmap(imageBitmap);
            }
        }


        listOfTodayTask = new ArrayList<>();

        DataBase dataBase = new DataBase(context);
        Cursor crsr = dataBase.getTask(dataBase, date);
        if (crsr.moveToFirst()) {
            do {
                String taskId = crsr.getString(2);
                String taskStatement = crsr.getString(3);
                String status = crsr.getString(4);
                String detail = crsr.getString(5);
                Task mTask = new Task(taskId, taskStatement, status, detail);
                listOfTodayTask.add(mTask);
            } while (crsr.moveToNext());

            currentTask = 0;
            FragmentTransaction transaction = FragManager.beginTransaction();
            taskListSize = listOfTodayTask.size();
            Task firstTask = listOfTodayTask.get(0);
            mTaskTurn.setText((currentTask + 1) + "/" + taskListSize);
            Bundle bundle = new Bundle();
            bundle.putString("data", "Yes");
            bundle.putString("taskId", firstTask.getTaskId());
            bundle.putString("taskstatement", firstTask.getTaskstatement());
            bundle.putString("detail", firstTask.getDetail());
            taskFragment taskInfo = new taskFragment();
            taskInfo.setArguments(bundle);
            transaction.replace(R.id.taskInfoFragment, taskInfo);
            transaction.commit();

        } else {
           // mTopBar.setVisibility(View.GONE);
            FragmentTransaction transaction = FragManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("data", "No");
            taskFragment taskInfo = new taskFragment();
            taskInfo.setArguments(bundle);
            transaction.replace(R.id.taskInfoFragment, taskInfo);
            transaction.commit();
        }


        mBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, CalenderDatePicker.class));
                finish();
            }
        });

        mPredecesoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentTask = (currentTask - 1 + taskListSize) % taskListSize;
                Task firstTask = listOfTodayTask.get(currentTask);
               // mTaskTurn.setText((currentTask + 1) + "/" + taskListSize);
                FragmentTransaction transaction = FragManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("data", "Yes");
                bundle.putString("taskId", firstTask.getTaskId());
                bundle.putString("taskstatement", firstTask.getTaskstatement());
                bundle.putString("detail", firstTask.getDetail());
                taskFragment taskInfo = new taskFragment();
                taskInfo.setArguments(bundle);
                transaction.replace(R.id.taskInfoFragment, taskInfo);
                transaction.commit();
            }
        });

        mSuccessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentTask = (currentTask + 1) % taskListSize;
                Task firstTask = listOfTodayTask.get(currentTask);
               // mTaskTurn.setText((currentTask + 1) + "/" + taskListSize);
                FragmentTransaction transaction = FragManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("data", "Yes");
                bundle.putString("taskId", firstTask.getTaskId());
                bundle.putString("taskstatement", firstTask.getTaskstatement());
                bundle.putString("detail", firstTask.getDetail());
                taskFragment taskInfo = new taskFragment();
                taskInfo.setArguments(bundle);
                transaction.replace(R.id.taskInfoFragment, taskInfo);
                transaction.commit();
            }
        });


    }


}
