package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by johncordeiro on 20/08/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Media implements Parcelable {

    public static final String KEY_DURATION = "duration";
    public static final String KEY_FILENAME = "filename";

    public enum Type {
        Picture,
        Video,
        VideoPhone,
        File,
        Audio
    }

    private String id;

    @Expose
    private String url;

    @Expose
    private Type type;

    private String thumbnail;

    private HashMap<String, Object> metadata;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Media() {
    }

    public Media(String id, String url, Type type, String name) {
        this.id = id;
        this.url = url;
        this.type = type;
    }

    public Media(LocalMedia localMedia) {
        this.type = localMedia.getType();
        this.metadata = localMedia.getMetadata();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Media media = (Media) o;
        return id != null ? id.equals(media.id) : super.equals(o);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.thumbnail);
        dest.writeSerializable(this.metadata);
    }

    protected Media(Parcel in) {
        this.id = in.readString();
        this.url = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Media.Type.values()[tmpType];
        this.thumbnail = in.readString();
        this.metadata = (HashMap<String, Object>) in.readSerializable();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
