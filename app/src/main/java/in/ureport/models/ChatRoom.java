package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by johncordeiro on 19/07/15.
 */
public abstract class ChatRoom implements Parcelable {

    private String lastMessage;

    private Date lastMessageDate;

    private Integer unreadMessages;

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public Integer getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(Integer unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lastMessage);
        dest.writeLong(lastMessageDate != null ? lastMessageDate.getTime() : -1);
        dest.writeValue(this.unreadMessages);
    }

    public ChatRoom() {
    }

    protected ChatRoom(Parcel in) {
        this.lastMessage = in.readString();
        long tmpLastMessageDate = in.readLong();
        this.lastMessageDate = tmpLastMessageDate == -1 ? null : new Date(tmpLastMessageDate);
        this.unreadMessages = (Integer) in.readValue(Integer.class.getClassLoader());
    }

}
