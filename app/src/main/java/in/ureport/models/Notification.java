package in.ureport.models;

import java.util.Date;

import in.ureport.listener.OnNotificationSelectedListener;

/**
 * Created by johncordeiro on 21/07/15.
 */
public class Notification {

    private String id;

    private String message;

    private Date date;

    private User user;

    private OnNotificationSelectedListener onNotificationSelectedListener;

    public Notification(String id, String message, Date date, User user) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.user = user;
    }

    public Notification() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OnNotificationSelectedListener getOnNotificationSelectedListener() {
        return onNotificationSelectedListener;
    }

    public void setOnNotificationSelectedListener(OnNotificationSelectedListener onNotificationSelectedListener) {
        this.onNotificationSelectedListener = onNotificationSelectedListener;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;
        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
