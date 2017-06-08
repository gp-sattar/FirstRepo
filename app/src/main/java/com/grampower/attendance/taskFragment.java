package com.grampower.attendance;

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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

/**
 * Created by samdroid on 2/6/17.
 */

public class taskFragment extends Fragment {

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

       final  View view= inflater.inflate(R.layout.task_fragment_view,container,false);
        final RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.frag_status);
        TextView mStatement=(TextView)view.findViewById(R.id.taskStatement_fragment);
        final EditText details=(EditText)view.findViewById(R.id.detail_fragment);
        Button mSubmit=(Button)view.findViewById(R.id.report_submit_button);
       Bundle args=getArguments();
        if(args==null){
            Toast.makeText(getActivity(),"arguments are null",Toast.LENGTH_LONG).show();
        }else{

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
        }


        return view;
    }


}
