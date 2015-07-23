package in.ureport.models;

import java.util.Date;

/**
 * Created by johncordeiro on 21/07/15.
 */
public class Notification {

    private String message;

    private Date date;

    private User user;

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
}
