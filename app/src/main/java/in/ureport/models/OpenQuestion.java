package in.ureport.models;

import android.os.Parcel;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class OpenQuestion extends PollQuestion {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public OpenQuestion() {
    }

    protected OpenQuestion(Parcel in) {
        super(in);
    }

    public static final Creator<OpenQuestion> CREATOR = new Creator<OpenQuestion>() {
        public OpenQuestion createFromParcel(Parcel source) {
            return new OpenQuestion(source);
        }

        public OpenQuestion[] newArray(int size) {
            return new OpenQuestion[size];
        }
    };
}
