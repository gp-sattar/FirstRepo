package com.grampower.attendance;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GetTodayTasks extends AppCompatActivity {

    int taskListSize;
    int currentTask;

    Context context;
    TextView mTaskTurn;
   FragmentManager FragManager;

   List<Task> listOfTodayTask;
    ImageButton mPredecesoor,mSuccessor,mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_today_tasks);
        context=this;
        mPredecesoor=(ImageButton)findViewById(R.id.predecessor);
        mSuccessor=(ImageButton)findViewById(R.id.successor);
        mTaskTurn=(TextView)findViewById(R.id.taskTurn);
        mBack=(ImageButton)findViewById(R.id.backAssignTask);
        FragManager = getFragmentManager();
        FragmentTransaction transaction = FragManager.beginTransaction();
        long date=System.currentTimeMillis();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd_MM_yy");
        String dateText=dateFormat.format(date);
        SharedPreferences sharedPreferences=context.getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
       String email=sharedPreferences.getString("email","");

         listOfTodayTask=new ArrayList<>();

        DataBase dataBase=new DataBase(context);
        Cursor crsr=dataBase.getTask(dataBase,dateText);
        if(crsr.moveToFirst()){
            do{
                String taskId=crsr.getString(2);
                String taskStatement=crsr.getString(3);
                String status=crsr.getString(4);
                String detail=crsr.getString(5);
                Task mTask=new Task(taskId,taskStatement,status,detail);

                Toast.makeText(context,"id :"+crsr.getString(0)+" and "+taskStatement,Toast.LENGTH_LONG);

                Log.d("Attendance","id :"+crsr.getString(0)+" and "+taskStatement);

                listOfTodayTask.add(mTask);
            }while(crsr.moveToNext());

            currentTask=0;
            taskListSize=listOfTodayTask.size();
            Task firstTask=listOfTodayTask.get(0);
            mTaskTurn.setText((currentTask+1)+"/"+taskListSize);
            Bundle bundle = new Bundle();
            bundle.putString("taskId",firstTask.getTaskId());
            bundle.putString("taskstatement",firstTask.getTaskstatement());
            bundle.putString("detail",firstTask.getDetail());
            taskFragment taskInfo = new taskFragment();
            taskInfo.setArguments(bundle);
            transaction.replace(R.id.taskInfoFragment, taskInfo);
            transaction.commit();

        }else{
            Log.d("Attendance","no Task for today updated yet");
        }


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,MainActivity.class));
                finish();
            }
        });

        mPredecesoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentTask=(currentTask-1+taskListSize)%taskListSize;
                Task firstTask=listOfTodayTask.get(currentTask);
                mTaskTurn.setText((currentTask+1)+"/"+taskListSize);
                FragmentTransaction transaction = FragManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("taskId",firstTask.getTaskId());
                bundle.putString("taskstatement",firstTask.getTaskstatement());
                bundle.putString("detail",firstTask.getDetail());
                taskFragment taskInfo = new taskFragment();
                taskInfo.setArguments(bundle);
                transaction.replace(R.id.taskInfoFragment, taskInfo);
                transaction.commit();
            }
        });

        mSuccessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentTask=(currentTask+1)%taskListSize;
                Task firstTask=listOfTodayTask.get(currentTask);
                mTaskTurn.setText((currentTask+1)+"/"+taskListSize);
                FragmentTransaction transaction = FragManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("taskId",firstTask.getTaskId());
                bundle.putString("taskstatement",firstTask.getTaskstatement());
                bundle.putString("detail",firstTask.getDetail());
                taskFragment taskInfo = new taskFragment();
                taskInfo.setArguments(bundle);
                transaction.replace(R.id.taskInfoFragment, taskInfo);
                transaction.commit();
            }
        });


    }


}
