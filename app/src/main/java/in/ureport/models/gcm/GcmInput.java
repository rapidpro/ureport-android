package in.ureport.models.gcm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by John Cordeiro on 5/1/17.
 * Copyright © 2017 Soloshot, Inc. All rights reserved.
 */

public class GcmInput<T extends NotificationHolder> {

    private enum Priority {
        normal,
        high,
    }

    @Expose
    private String to;

    @Expose
    private T data;

    @Expose
    private Priority priority = Priority.high;

    @Expose
    @SerializedName("collapse_key")
    private String collapseKey;

    @Expose
    private Notification notification;

    public GcmInput(String to, T data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getCollapseKey() {
        return collapseKey;
    }

    public GcmInput setCollapseKey(String collapseKey) {
        this.collapseKey = collapseKey;
        return this;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
