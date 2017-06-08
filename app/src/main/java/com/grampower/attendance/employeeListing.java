package com.grampower.attendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class employeeListing extends AppCompatActivity implements RCVItemClcikListener{

    ArrayList<EmployeeWrapper> listOfEmployees=null;
    RecyclerView recyclerView;
    ImageButton mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_listing);
        recyclerView=(RecyclerView)findViewById(R.id.employeeRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBack=(ImageButton)findViewById(R.id.back_employee_listing);
         listOfEmployees=new ArrayList<>();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name=dataSnapshot.child("profile").child("name").getValue().toString();
                String email=dataSnapshot.getKey();
                Log.d("azad",email);
                EmployeeWrapper employee=new EmployeeWrapper(email,name);
                listOfEmployees.add(employee);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(employeeListing.this,MainActivity.class));
                finish();
            }
        });

        new waitForList().execute();

    }

   void setEmployeeAdapter(){
       EmployeeAdapter adapter=new EmployeeAdapter(this,listOfEmployees);
       recyclerView.setAdapter(adapter);
       adapter.setRCVItemClickListener(this);
   }

    @Override
    public void OnItemClick(View clickView, int position) {
        EmployeeWrapper employee=listOfEmployees.get(position);

        Log.d("azad","itemclicked");
         switch (clickView.getId()){

             case R.id.newTaskButton:

                 Log.d("azad","buttonclicked");

                    Intent intent=new Intent(employeeListing.this,AssignTask.class);
                     intent.putExtra("email",employee.getEmail());
                     intent.putExtra("name",employee.getName());
                     startActivity(intent);
                     finish();
                 break;

             case R.id.reportButton:
                 break;
         }
    }


    class waitForList extends AsyncTask<Void,Void,Void> {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog =new ProgressDialog(employeeListing.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

        }
        @Override
        protected Void doInBackground(Void... params) {
            while (listOfEmployees.size() == 0) ;
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            setEmployeeAdapter();

        }

    }
}
