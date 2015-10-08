package in.ureport.models.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by johncordeiro on 07/10/15.
 */
@Table(name = "MessageNotification")
public class MessageNotification extends Model {

    @Column(name = "message")
    private String message;

    @Column(name = "date")
    private Date date;

    public MessageNotification(String message, Date date) {
        this.message = message;
        this.date = date;
    }

    public MessageNotification() {
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
}
