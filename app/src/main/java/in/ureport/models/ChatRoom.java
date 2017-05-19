package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;

/**
 * Created by johncordeiro on 19/07/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoom implements Parcelable {

    public enum Type {
        Individual,
        Group
    }

    @Expose
    private String key;

    private Integer unreadMessages;

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(Integer unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public ChatRoom() {
    }

    public ChatRoom(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatRoom chatRoom = (ChatRoom) o;
        return key.equals(chatRoom.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "key='" + key + '\'' +
                ", unreadMessages=" + unreadMessages +
                ", type=" + type +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeValue(this.unreadMessages);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    protected ChatRoom(Parcel in) {
        this.key = in.readString();
        this.unreadMessages = (Integer) in.readValue(Integer.class.getClassLoader());
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : ChatRoom.Type.values()[tmpType];
    }

    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        public ChatRoom createFromParcel(Parcel source) {
            return new ChatRoom(source);
        }

        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };
}
