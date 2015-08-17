package in.ureport.models;

import java.util.Date;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatMessage {

    private String key;

    private String message;

    private Date date;

    private User user;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

}
