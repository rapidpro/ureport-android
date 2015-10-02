package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

/**
 * Created by johncordeiro on 01/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category implements Parcelable {

    @SerializedName("image_url")
    private String imageUrl;

    private String name;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageUrl);
        dest.writeString(this.name);
    }

    public Category() {
    }

    protected Category(Parcel in) {
        this.imageUrl = in.readString();
        this.name = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
