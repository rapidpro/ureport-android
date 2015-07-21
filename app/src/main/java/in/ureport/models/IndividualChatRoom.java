package in.ureport.models;

import android.os.Parcel;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class IndividualChatRoom extends ChatRoom {

    private User friend;

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.friend, 0);
    }

    public IndividualChatRoom() {
    }

    protected IndividualChatRoom(Parcel in) {
        super(in);
        this.friend = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<IndividualChatRoom> CREATOR = new Creator<IndividualChatRoom>() {
        public IndividualChatRoom createFromParcel(Parcel source) {
            return new IndividualChatRoom(source);
        }

        public IndividualChatRoom[] newArray(int size) {
            return new IndividualChatRoom[size];
        }
    };
}
