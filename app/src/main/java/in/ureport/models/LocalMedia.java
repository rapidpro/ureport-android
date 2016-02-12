package in.ureport.models;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by johncordeiro on 02/09/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalMedia extends Media {

    private Uri path;

    private Bitmap thumbnail;

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.path, 0);
        dest.writeParcelable(this.thumbnail, 0);
    }

    public LocalMedia() {
    }

    public LocalMedia(Uri path) {
        this.path = path;
    }

    protected LocalMedia(Parcel in) {
        super(in);
        this.path = in.readParcelable(Uri.class.getClassLoader());
        this.thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<LocalMedia> CREATOR = new Creator<LocalMedia>() {
        public LocalMedia createFromParcel(Parcel source) {
            return new LocalMedia(source);
        }

        public LocalMedia[] newArray(int size) {
            return new LocalMedia[size];
        }
    };
}
