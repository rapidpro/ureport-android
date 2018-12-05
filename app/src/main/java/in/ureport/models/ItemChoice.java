package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johncordeiro on 14/09/15.
 */
public class ItemChoice implements Parcelable {

    private String title;

    private Integer value;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = Integer.parseInt(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeValue(this.value);
    }

    public ItemChoice() {
    }

    protected ItemChoice(Parcel in) {
        this.title = in.readString();
        this.value = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ItemChoice> CREATOR = new Creator<ItemChoice>() {
        public ItemChoice createFromParcel(Parcel source) {
            return new ItemChoice(source);
        }

        public ItemChoice[] newArray(int size) {
            return new ItemChoice[size];
        }
    };
}
