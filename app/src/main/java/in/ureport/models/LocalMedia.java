package in.ureport.models;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;

/**
 * Created by johncordeiro on 02/09/15.
 */
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
