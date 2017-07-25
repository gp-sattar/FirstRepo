package com.grampower.attendance.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.grampower.attendance.Adapters.TaskOperationsAdapter;
import com.grampower.attendance.R;
import com.grampower.attendance.pojos.ChildTask;
import com.grampower.attendance.pojos.TaskOperationWrapper;

import java.util.ArrayList;
import java.util.List;

public class TaskRelatedActions extends AppCompatActivity {

    RecyclerView mRecyclerView;
    List<TaskOperationWrapper> taskOperationList;
    TaskOperationsAdapter adapter;
    ImageButton mBack;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_related_actions);
        context = this;
        mBack = (ImageButton) findViewById(R.id.ib_back_task_operation);
        taskOperationList = new ArrayList<>();

        List<ChildTask> childlist1 = new ArrayList<>();
        List<ChildTask> childlist2 = new ArrayList<>();
        childlist2.add(new ChildTask("Date Wise", ""));
        childlist2.add(new ChildTask("Today", ""));
        childlist2.add(new ChildTask("Self Assigned", ""));
        childlist2.add(new ChildTask("Pending Tasks", ""));

        taskOperationList.add(new TaskOperationWrapper("Self Assign", childlist1));
        taskOperationList.add(new TaskOperationWrapper("Task View", childlist2));


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_task_operations);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new TaskOperationsAdapter(taskOperationList, context);
        adapter.toggleGroup(1);
        mRecyclerView.setAdapter(adapter);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,MainActivity.class));
                finish();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(context,MainActivity.class));
        finish();
    }
}
