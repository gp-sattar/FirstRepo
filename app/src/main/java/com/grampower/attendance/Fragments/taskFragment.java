package com.grampower.attendance.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grampower.attendance.Activities.GetTodayTasks;
import com.grampower.attendance.R;

import java.text.SimpleDateFormat;

/**
 * Created by samdroid on 2/6/17.
 */

public class taskFragment extends Fragment {

    View view;

    Context context;
    public taskFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
           this.context= (GetTodayTasks) context;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
   String taskStatement="Loading error",detail="";


        Bundle args=getArguments();
        String dataAvailable=args.getString("data");
        if(dataAvailable.equals("Yes")) {
            view= inflater.inflate(R.layout.task_fragment_view,container,false);
            final RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.frag_status);
            TextView mStatement=(TextView)view.findViewById(R.id.taskStatement_fragment);
            final EditText details=(EditText)view.findViewById(R.id.detail_fragment);
            Button mSubmit=(Button)view.findViewById(R.id.report_submit_button);
            taskStatement=args.getString("taskstatement");
            detail=args.getString("detail");
            final String taskId=args.getString("taskId");

            mStatement.setText(taskStatement);
            details.setHint(detail);
            mSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton status = (RadioButton)view.findViewById(selectedId);
                    String mStatus=status.getText().toString();
                    String detail=details.getText().toString().trim();
                    SharedPreferences sharedPreferences=getActivity().getSharedPreferences("MYPREFERENCES",Context.MODE_PRIVATE);
                    String email=sharedPreferences.getString("email","");
                    long date=System.currentTimeMillis();
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd_MM_yy");
                    String datetext=dateFormat.format(date);
                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
                    databaseReference.child(email).child("tasks").child(datetext).child(taskId).child("status").setValue(mStatus);
                    databaseReference.child(email).child("tasks").child(datetext).child(taskId).child("detail").setValue(detail);

                }
            });

        }else{
            view = inflater.inflate(R.layout.no_task_fragment_view, container, false);
        }




        return view;
    }


}
