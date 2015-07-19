package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class PollCategory implements Parcelable {

    private String name;

    private @DrawableRes int icon;

    private @ColorRes int color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.icon);
        dest.writeInt(this.color);
    }

    public PollCategory() {
    }

    protected PollCategory(Parcel in) {
        this.name = in.readString();
        this.icon = in.readInt();
        this.color = in.readInt();
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
