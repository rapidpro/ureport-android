package in.ureport.models;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by johncordeiro on 19/07/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndividualChatRoom extends ChatRoom {

    private Date createdDate;

    private String blocked;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
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
        dest.writeLong(createdDate != null ? createdDate.getTime() : -1);
        dest.writeString(this.blocked);
    }

    protected IndividualChatRoom(Parcel in) {
        super(in);
        long tmpCreatedDate = in.readLong();
        this.createdDate = tmpCreatedDate == -1 ? null : new Date(tmpCreatedDate);
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
