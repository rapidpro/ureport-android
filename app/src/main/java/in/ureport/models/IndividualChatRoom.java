package in.ureport.models;

import android.os.Parcel;

import java.util.Date;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class IndividualChatRoom extends ChatRoom {

    private long createdDate;

    private String blocked;

    public Date getCreatedDate() {
        return new Date(createdDate);
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    public IndividualChatRoom() {
        setType(Type.Individual);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(createdDate);
        dest.writeString(this.blocked);
    }

    protected IndividualChatRoom(Parcel in) {
        super(in);
        this.createdDate = in.readLong();
        this.blocked = in.readString();
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
