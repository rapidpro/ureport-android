package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class PollCategory implements Parcelable {

    private String name;

    private String image_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PollCategory that = (PollCategory) o;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.image_url);
    }

    public PollCategory() {
    }

    protected PollCategory(Parcel in) {
        this.name = in.readString();
        this.image_url = in.readString();
    }

    public static final Creator<PollCategory> CREATOR = new Creator<PollCategory>() {
        public PollCategory createFromParcel(Parcel source) {
            return new PollCategory(source);
        }

        public PollCategory[] newArray(int size) {
            return new PollCategory[size];
        }
    };
}
