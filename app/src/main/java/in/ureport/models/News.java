package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class News implements Parcelable {

    private Integer id;

    private String title;

    private Boolean featured;

    private String summary;

    @SerializedName("video_id")
    private String videoId;

    @SerializedName("audio_link")
    private String audioLink;

    private String tags;

    private Integer org;

    private List<String> images;

    private Category category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getOrg() {
        return org;
    }

    public void setOrg(Integer org) {
        this.org = org;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        News news = (News) o;

        return id.equals(news.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeValue(this.featured);
        dest.writeString(this.summary);
        dest.writeString(this.videoId);
        dest.writeString(this.audioLink);
        dest.writeString(this.tags);
        dest.writeValue(this.org);
        dest.writeStringList(this.images);
        dest.writeParcelable(this.category, flags);
    }

    public News() {
    }

    protected News(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.featured = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.summary = in.readString();
        this.videoId = in.readString();
        this.audioLink = in.readString();
        this.tags = in.readString();
        this.org = (Integer) in.readValue(Integer.class.getClassLoader());
        this.images = in.createStringArrayList();
        this.category = in.readParcelable(Category.class.getClassLoader());
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", featured=" + featured +
                ", summary='" + summary + '\'' +
                ", videoId='" + videoId + '\'' +
                ", audioLink='" + audioLink + '\'' +
                ", tags='" + tags + '\'' +
                ", org=" + org +
                ", images=" + images +
                ", category=" + category +
                '}';
    }
}
