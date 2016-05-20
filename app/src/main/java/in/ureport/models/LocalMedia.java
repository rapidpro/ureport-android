package in.ureport.models;

import android.net.Uri;
import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by johncordeiro on 02/09/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalMedia extends Media {

    private Uri path;

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.path, 0);
    }

    public LocalMedia() {
    }

    public LocalMedia(Uri path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        LocalMedia that = (LocalMedia) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    protected LocalMedia(Parcel in) {
        super(in);
        this.path = in.readParcelable(Uri.class.getClassLoader());
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
