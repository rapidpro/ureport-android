package in.ureport.models;

import android.os.Parcel;

import java.util.Date;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class IndividualChatRoom extends ChatRoom {

    private Date createdDate;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(createdDate != null ? createdDate.getTime() : -1);
    }

    public IndividualChatRoom() {
        setType(Type.Individual);
    }

    protected IndividualChatRoom(Parcel in) {
        super(in);
        long tmpCreatedDate = in.readLong();
        this.createdDate = tmpCreatedDate == -1 ? null : new Date(tmpCreatedDate);
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
