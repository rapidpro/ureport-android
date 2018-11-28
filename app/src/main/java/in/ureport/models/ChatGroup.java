package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatGroup implements Parcelable {

    private String title;

    private String description;

    private String picture;

    private Date creationDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.picture);
        dest.writeLong(creationDate != null ? creationDate.getTime() : -1);
    }

    public ChatGroup() {
    }

    protected ChatGroup(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.picture = in.readString();
        long tmpCreationDate = in.readLong();
        this.creationDate = tmpCreationDate == -1 ? null : new Date(tmpCreationDate);
    }

    public static final Creator<ChatGroup> CREATOR = new Creator<ChatGroup>() {
        public ChatGroup createFromParcel(Parcel source) {
            return new ChatGroup(source);
        }

        public ChatGroup[] newArray(int size) {
            return new ChatGroup[size];
        }
    };
}
