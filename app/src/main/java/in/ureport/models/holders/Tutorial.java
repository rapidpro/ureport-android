package in.ureport.models.holders;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

/**
 * Created by johncordeiro on 05/10/15.
 */
public class Tutorial implements Parcelable {

    private String title;

    private String description;

    private @DrawableRes int image;

    private @ColorRes int background;

    public Tutorial(String title, String description, int image, int background) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.background = background;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeInt(this.image);
        dest.writeInt(this.background);
    }

    protected Tutorial(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.image = in.readInt();
        this.background = in.readInt();
    }

    public static final Creator<Tutorial> CREATOR = new Creator<Tutorial>() {
        public Tutorial createFromParcel(Parcel source) {
            return new Tutorial(source);
        }

        public Tutorial[] newArray(int size) {
            return new Tutorial[size];
        }
    };
}
