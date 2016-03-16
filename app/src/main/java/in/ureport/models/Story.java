package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;

/**
 * Created by johncordeiro on 7/14/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Story implements Parcelable {

    @Expose
    private String key;

    private String title;

    private String content;

    private Date createdDate;

    private String user;

    private User userObject;

    private Integer contributions;

    @JsonIgnore
    private Integer likes;

    private String markers;

    private List<Media> medias;

    private Media cover;

    public Story() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public User getUserObject() {
        return userObject;
    }

    public void setUserObject(User userObject) {
        this.userObject = userObject;
    }

    public Integer getContributions() {
        return contributions;
    }

    public void setContributions(Integer contributions) {
        this.contributions = contributions;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getMarkers() {
        return markers;
    }

    public void setMarkers(String markers) {
        this.markers = markers;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public Media getCover() {
        return cover;
    }

    public void setCover(Media cover) {
        this.cover = cover;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeLong(createdDate != null ? createdDate.getTime() : -1);
        dest.writeString(this.user);
        dest.writeParcelable(this.userObject, 0);
        dest.writeValue(this.contributions);
        dest.writeValue(this.likes);
        dest.writeString(this.markers);
        dest.writeTypedList(medias);
        dest.writeParcelable(this.cover, 0);
    }

    protected Story(Parcel in) {
        this.key = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        long tmpCreatedDate = in.readLong();
        this.createdDate = tmpCreatedDate == -1 ? null : new Date(tmpCreatedDate);
        this.user = in.readString();
        this.userObject = in.readParcelable(User.class.getClassLoader());
        this.contributions = (Integer) in.readValue(Integer.class.getClassLoader());
        this.likes = (Integer) in.readValue(Integer.class.getClassLoader());
        this.markers = in.readString();
        this.medias = in.createTypedArrayList(Media.CREATOR);
        this.cover = in.readParcelable(Media.class.getClassLoader());
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        public Story[] newArray(int size) {
            return new Story[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Story story = (Story) o;
        return key != null ? key.equals(story.key) : story.key == null;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
