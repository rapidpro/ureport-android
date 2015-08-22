package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatMessage implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.message);
        dest.writeLong(date != null ? date.getTime() : -1);
        dest.writeParcelable(this.user, 0);
    }

    public ChatMessage() {
    }

    protected ChatMessage(Parcel in) {
        this.key = in.readString();
        this.message = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    @Override
    public String toString() {
        return "ChatMessage{" +
                "key='" + key + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", user=" + user +
                '}';
    }
}
