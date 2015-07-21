package in.ureport.models;

import android.os.Parcel;

import java.util.List;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class GroupChatRoom extends ChatRoom {

    private ChatGroup chatGroup;

    private List<User> participants;

    public ChatGroup getChatGroup() {
        return chatGroup;
    }

    public void setChatGroup(ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.chatGroup, flags);
        dest.writeTypedList(participants);
    }

    public GroupChatRoom() {
    }

    protected GroupChatRoom(Parcel in) {
        super(in);
        this.chatGroup = in.readParcelable(ChatGroup.class.getClassLoader());
        this.participants = in.createTypedArrayList(User.CREATOR);
    }

    public static final Creator<GroupChatRoom> CREATOR = new Creator<GroupChatRoom>() {
        public GroupChatRoom createFromParcel(Parcel source) {
            return new GroupChatRoom(source);
        }

        public GroupChatRoom[] newArray(int size) {
            return new GroupChatRoom[size];
        }
    };
}
