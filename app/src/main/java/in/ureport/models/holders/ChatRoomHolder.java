package in.ureport.models.holders;

import android.os.Parcel;
import android.os.Parcelable;

import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;

/**
 * Created by johncordeiro on 16/08/15.
 */
public class ChatRoomHolder implements Parcelable {

    public ChatRoom chatRoom;

    public ChatMembers members;

    public ChatMessage lastMessage;

    public ChatRoomHolder(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public ChatRoomHolder(ChatRoom chatRoom, ChatMembers members, ChatMessage lastMessage) {
        this.chatRoom = chatRoom;
        this.members = members;
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatRoomHolder that = (ChatRoomHolder) o;
        return chatRoom.equals(that.chatRoom);
    }

    @Override
    public int hashCode() {
        return chatRoom.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.chatRoom, 0);
        dest.writeParcelable(this.members, 0);
        dest.writeParcelable(this.lastMessage, 0);
    }

    protected ChatRoomHolder(Parcel in) {
        this.chatRoom = in.readParcelable(ChatRoom.class.getClassLoader());
        this.members = in.readParcelable(ChatMembers.class.getClassLoader());
        this.lastMessage = in.readParcelable(ChatMessage.class.getClassLoader());
    }

    public static final Creator<ChatRoomHolder> CREATOR = new Creator<ChatRoomHolder>() {
        public ChatRoomHolder createFromParcel(Parcel source) {
            return new ChatRoomHolder(source);
        }

        public ChatRoomHolder[] newArray(int size) {
            return new ChatRoomHolder[size];
        }
    };
}
