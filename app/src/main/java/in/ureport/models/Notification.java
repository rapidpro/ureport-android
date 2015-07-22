package in.ureport.models;

import java.util.Date;

/**
 * Created by johncordeiro on 21/07/15.
 */
public class Notification {

    private String message;

    private Date date;

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

}
