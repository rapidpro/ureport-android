package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by johncordeiro on 7/14/15.
 */
@Table(name = "Story")
public class Story extends Model implements Parcelable {

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "createdDate")
    private Date createdDate;

    @Column(name = "author")
    private User user;

    @Column(name = "contributions")
    private Integer contributions;

    @Column(name = "markers")
    private String markers;

    @Column(name = "image")
    private Integer image;

    public Story() {
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

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeLong(createdDate != null ? createdDate.getTime() : -1);
        dest.writeParcelable(this.user, 0);
        dest.writeValue(this.contributions);
        dest.writeString(this.markers);
        dest.writeValue(this.image);
    }

    protected Story(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        long tmpCreatedDate = in.readLong();
        this.createdDate = tmpCreatedDate == -1 ? null : new Date(tmpCreatedDate);
        this.user = in.readParcelable(User.class.getClassLoader());
        this.contributions = (Integer) in.readValue(Integer.class.getClassLoader());
        this.markers = in.readString();
        this.image = (Integer) in.readValue(Integer.class.getClassLoader());
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
