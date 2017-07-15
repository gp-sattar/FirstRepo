package com.grampower.attendance.pojos;

/**
 * Created by samdroid on 12/6/17.
 */

public class NotificationWrapper {
    String key,notification;

    public NotificationWrapper(String key, String notification) {
        this.key = key;
        this.notification = notification;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
