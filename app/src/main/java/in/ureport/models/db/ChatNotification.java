package in.ureport.models.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by johncordeiro on 21/08/15.
 */
@Table(name = "ChatNotification")
public class ChatNotification extends Model {

    @Column(name = "chatRoomId")
    private String chatRoomId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "message")
    private String message;

    @Column(name = "date")
    private Date date;

    public ChatNotification(String chatRoomId, String nickname, String message, Date date) {
        this.chatRoomId = chatRoomId;
        this.nickname = nickname;
        this.message = message;
        this.date = date;
    }

    public ChatNotification() {
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
