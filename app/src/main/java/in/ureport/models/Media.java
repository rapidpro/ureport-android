package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johncordeiro on 20/08/15.
 */
public class Media implements Parcelable {

    private Integer id;

    private String url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
    }

    public Media() {
    }

    public Media(Integer id, String url) {
        this.id = id;
        this.url = url;
    }

    protected Media(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
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
