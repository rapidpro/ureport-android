package in.ureport.models;

import android.os.Parcel;

import java.util.Date;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class GroupChatRoom extends ChatRoom {

    private String title;

    private String description;

    private String picture;

    private Date creationDate;

    private Boolean privateAccess;

    private Boolean mediaAllowed;

    private User administrator;

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

    public Boolean getPrivateAccess() {
        return privateAccess;
    }

    public void setPrivateAccess(Boolean privateAccess) {
        this.privateAccess = privateAccess;
    }

    public User getAdministrator() {
        return administrator;
    }

    public void setAdministrator(User administrator) {
        this.administrator = administrator;
    }

    public Boolean getMediaAllowed() {
        return mediaAllowed;
    }

    public void setMediaAllowed(Boolean mediaAllowed) {
        this.mediaAllowed = mediaAllowed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.picture);
        dest.writeLong(creationDate != null ? creationDate.getTime() : -1);
        dest.writeValue(this.privateAccess);
        dest.writeValue(this.mediaAllowed);
        dest.writeParcelable(this.administrator, 0);
    }

    public GroupChatRoom() {
        setType(Type.Group);
    }

    protected GroupChatRoom(Parcel in) {
        super(in);
        this.title = in.readString();
        this.description = in.readString();
        this.picture = in.readString();
        long tmpCreationDate = in.readLong();
        this.creationDate = tmpCreationDate == -1 ? null : new Date(tmpCreationDate);
        this.privateAccess = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mediaAllowed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.administrator = in.readParcelable(User.class.getClassLoader());
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
