package in.ureport.models;

import android.os.Parcel;

/**
 * Created by johncordeiro on 02/09/15.
 */
public class VideoMedia extends Media {

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.path);
    }

    public VideoMedia() {
        setType(Type.Video);
    }

    public VideoMedia(String path) {
        this();
        this.path = path;
    }

    protected VideoMedia(Parcel in) {
        super(in);
        this.path = in.readString();
    }

    public static final Creator<VideoMedia> CREATOR = new Creator<VideoMedia>() {
        public VideoMedia createFromParcel(Parcel source) {
            return new VideoMedia(source);
        }

        public VideoMedia[] newArray(int size) {
            return new VideoMedia[size];
        }
    };
}
