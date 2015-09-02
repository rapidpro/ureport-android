package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class Story extends Model implements Parcelable {

    private String key;

    private String title;

    private String content;

    private Date createdDate;

    private User user;

    private Integer contributions;

    private String markers;

    private String image;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getContributions() {
        return contributions;
    }

    public void setContributions(Integer contributions) {
        this.contributions = contributions;
    }

    public String getMarkers() {
        return markers;
    }

    public void setMarkers(String markers) {
        this.markers = markers;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        dest.writeParcelable(this.user, 0);
        dest.writeValue(this.contributions);
        dest.writeString(this.markers);
        dest.writeString(this.image);
        dest.writeTypedList(medias);
        dest.writeParcelable(this.cover, 0);
    }

    protected Story(Parcel in) {
        this.key = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        long tmpCreatedDate = in.readLong();
        this.createdDate = tmpCreatedDate == -1 ? null : new Date(tmpCreatedDate);
        this.user = in.readParcelable(User.class.getClassLoader());
        this.contributions = (Integer) in.readValue(Integer.class.getClassLoader());
        this.markers = in.readString();
        this.image = in.readString();
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
}
