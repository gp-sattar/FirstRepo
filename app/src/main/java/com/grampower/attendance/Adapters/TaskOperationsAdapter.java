package com.grampower.attendance.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.grampower.attendance.Activities.CalenderDatePicker;
import com.grampower.attendance.R;
import com.grampower.attendance.pojos.ChildTask;
import com.grampower.attendance.pojos.TaskOperationWrapper;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

/**
 * Created by sam on 17/7/17.
 */

public class TaskOperationsAdapter extends ExpandableRecyclerViewAdapter<TaskOperationsAdapter.MainTaskViewHolder, TaskOperationsAdapter.ChildTaskViewHolder> {

    List<TaskOperationWrapper>   taskGroups;
    Context context;

    public TaskOperationsAdapter(List<TaskOperationWrapper> groups,Context context) {
        super(groups);
        taskGroups=groups;
        this.context=context;
    }

    @Override
    public MainTaskViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.main_task_recycler_view, parent, false);
        return new MainTaskViewHolder(view);
    }

    @Override
    public ChildTaskViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.child_task_recylcerview, parent, false);
        return new ChildTaskViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ChildTaskViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
      // ChildTask childTask=taskGroups.get(flatPosition).getChildTasksList().get(childIndex);
        ChildTask childTask=(ChildTask) group.getItems().get(childIndex);
        holder.tvChildTaskName.setText(childTask.getChildTaskName());
        if(childIndex==group.getItems().size()-1){
            holder.vLowerLine.setVisibility(View.INVISIBLE);
        }
          if(flatPosition==2&&childIndex==0){
            holder.itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, CalenderDatePicker.class));
                }
            });

          }

    }

    @Override
    public void onBindGroupViewHolder(MainTaskViewHolder holder, int flatPosition, ExpandableGroup group) {
        TaskOperationWrapper taskOperationWrapper=taskGroups.get(flatPosition);
        holder.tvTaskName.setText(taskOperationWrapper.getTaskTitle());
        if(flatPosition==taskGroups.size()-1){
            holder.vLowerLineMain.setVisibility(View.INVISIBLE);
        }
    }

    public class MainTaskViewHolder extends GroupViewHolder {

        View  vUpperLineMain,vLowerLineMain;
        private TextView tvTaskName;


        public MainTaskViewHolder(View itemView) {
            super(itemView);
            vUpperLineMain=(View)itemView.findViewById(R.id.uppper_line_main);
            vLowerLineMain=(View)itemView.findViewById(R.id.lower_line_main);
            tvTaskName=(TextView)itemView.findViewById(R.id.tv_task_name_main);
        }


    }


    public class ChildTaskViewHolder extends ChildViewHolder {

        View itemview;
        View  vUpperLine,vLowerLine;
        private TextView tvChildTaskName;

        public ChildTaskViewHolder(View itemView) {
            super(itemView);
            vUpperLine=(View)itemView.findViewById(R.id.uppper_line);
            vLowerLine=(View)itemView.findViewById(R.id.lower_line);
            tvChildTaskName=(TextView)itemView.findViewById(R.id.tv_task_name);
             itemview=itemView;
            this.itemview.setClickable(true);

        }

    }

}
