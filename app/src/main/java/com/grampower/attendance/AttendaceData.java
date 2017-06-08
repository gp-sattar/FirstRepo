package com.grampower.attendance;

/**
 * Created by samdroid on 29/5/17.
 */

public class AttendaceData {

    String currentTime,selfieUrl,currentLocation;

    public AttendaceData(String currentTime, String selfieUrl, String currentLocation) {
        this.currentTime = currentTime;
        this.selfieUrl = selfieUrl;
        this.currentLocation = currentLocation;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getSelfieUrl() {
        return selfieUrl;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public void setSelfieUrl(String selfieUrl) {
        this.selfieUrl = selfieUrl;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
}
