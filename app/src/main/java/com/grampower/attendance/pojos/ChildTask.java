package com.grampower.attendance.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sam on 17/7/17.
 */

public class ChildTask implements Parcelable {

    String childTaskName, getChildTaskUrl;

    public ChildTask(String childTaskName, String getChildTaskUrl) {
        this.childTaskName = childTaskName;
        this.getChildTaskUrl = getChildTaskUrl;
    }

    protected ChildTask(Parcel in) {
        childTaskName = in.readString();
        getChildTaskUrl = in.readString();
    }

    public static final Creator<ChildTask> CREATOR = new Creator<ChildTask>() {
        @Override
        public ChildTask createFromParcel(Parcel in) {
            return new ChildTask(in);
        }

        @Override
        public ChildTask[] newArray(int size) {
            return new ChildTask[size];
        }
    };


    public String getChildTaskName() {
        return childTaskName;
    }

    public void setChildTaskName(String childTaskName) {
        this.childTaskName = childTaskName;
    }

    public String getGetChildTaskUrl() {
        return getChildTaskUrl;
    }

    public void setGetChildTaskUrl(String getChildTaskUrl) {
        this.getChildTaskUrl = getChildTaskUrl;
    }


    @Override
    public int describeContents() {
        return 2;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(childTaskName);
        dest.writeString(getChildTaskUrl);
    }
}
