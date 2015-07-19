package in.ureport.models;

import java.util.Date;

/**
 * Created by johncordeiro on 19/07/15.
 */
public abstract class ChatRoom {

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
}
