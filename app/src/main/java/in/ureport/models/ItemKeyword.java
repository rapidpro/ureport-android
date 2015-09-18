package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by johncordeiro on 14/09/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemKeyword implements Parcelable {

    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.keyword);
    }

    public ItemKeyword() {
    }

    protected ItemKeyword(Parcel in) {
        this.keyword = in.readString();
    }

    public static final Creator<ItemKeyword> CREATOR = new Creator<ItemKeyword>() {
        public ItemKeyword createFromParcel(Parcel source) {
            return new ItemKeyword(source);
        }

        public ItemKeyword[] newArray(int size) {
            return new ItemKeyword[size];
        }
    };

    @Override
    public String toString() {
        return keyword;
    }
}
