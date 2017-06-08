package com.grampower.attendance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by samdroid on 29/5/17.
 */

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.viewHolder> {
      Context context;

    private RCVItemClcikListener rcvItemClcikListener;

     ArrayList<EmployeeWrapper> listOfEmployee;
    public EmployeeAdapter(Context con,ArrayList<EmployeeWrapper> list) {
       this.listOfEmployee=list;
        this.context=con;
    }

    void setRCVItemClickListener(RCVItemClcikListener rcv){
    this.rcvItemClcikListener=rcv;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
   View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_adapter_view,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        EmployeeWrapper employee=listOfEmployee.get(position);
         String nameEmployee=employee.getName();
          String emailEmployee=employee.getEmail();
         holder.mNameView.setText(nameEmployee);
    }

    @Override
    public int getItemCount() {
        return listOfEmployee.size();
    }

    class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mNameView;
        Button mReportButton,mNewTaskButton;
        public viewHolder(View itemView) {
            super(itemView);
            mNameView=(TextView)itemView.findViewById(R.id.nameView);
            mReportButton=(Button)itemView.findViewById(R.id.reportButton);
            mNewTaskButton=(Button)itemView.findViewById(R.id.newTaskButton);

            mReportButton.setOnClickListener(this);
            mNewTaskButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(rcvItemClcikListener!=null){
                rcvItemClcikListener.OnItemClick(v,getAdapterPosition());
            }
        }
    }
}
