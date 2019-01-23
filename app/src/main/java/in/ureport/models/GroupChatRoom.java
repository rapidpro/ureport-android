package in.ureport.models;

import android.os.Parcel;

import java.util.Date;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class GroupChatRoom extends ChatRoom {

    private String title;

    private String subject;

    private Media picture;

    private long createdDate;

    private Boolean privateAccess;

    private Boolean mediaAllowed;

    private User administrator;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Media getPicture() {
        return picture;
    }

    public void setPicture(Media picture) {
        this.picture = picture;
    }

    public Date getCreatedDate() {
        return new Date(createdDate);
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
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
        dest.writeString(this.subject);
        dest.writeParcelable(this.picture, 0);
        dest.writeLong(createdDate);
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
        this.subject = in.readString();
        this.picture = in.readParcelable(Media.class.getClassLoader());
        this.createdDate = in.readLong();
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
