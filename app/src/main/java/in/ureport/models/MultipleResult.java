package in.ureport.models;

import android.os.Parcel;
import android.support.annotation.DrawableRes;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class MultipleResult extends PollResult {

    private @DrawableRes int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.image);
    }

    public MultipleResult() {
    }

    protected MultipleResult(Parcel in) {
        super(in);
        this.image = in.readInt();
    }

    public static final Creator<MultipleResult> CREATOR = new Creator<MultipleResult>() {
        public MultipleResult createFromParcel(Parcel source) {
            return new MultipleResult(source);
        }

        public MultipleResult[] newArray(int size) {
            return new MultipleResult[size];
        }
    };
}
