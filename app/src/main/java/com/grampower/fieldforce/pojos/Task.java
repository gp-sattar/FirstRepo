package com.grampower.fieldforce.pojos;

/**
 * Created by samdroid on 29/5/17.
 */

public class Task {
    String taskId,taskstatement,status,detail;

    public Task(String id,String taskstatement,String status,String detail) {
        this.taskId=id;
        this.taskstatement = taskstatement;
        this.status = status;
        this.detail=detail;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskstatement() {
        return taskstatement;
    }

    public void setTaskstatement(String taskstatement) {
        this.taskstatement = taskstatement;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String details) {
        this.detail= details;
    }
}
