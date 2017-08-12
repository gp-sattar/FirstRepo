package com.grampower.fieldforce.pojos;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by sam on 17/7/17.
 */

public class TaskOperationWrapper extends ExpandableGroup<ChildTask> {

    String taskTitle;
    List<ChildTask> childTasksList;

    public TaskOperationWrapper(String title, List<ChildTask> items) {
        super(title, items);
        taskTitle=title;
        childTasksList=items;
    }

    protected TaskOperationWrapper(Parcel in) {
        super(in);
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public List<ChildTask> getChildTasksList() {
        return childTasksList;
    }

    public void setChildTasksList(List<ChildTask> childTasksList) {
        this.childTasksList = childTasksList;
    }

}
