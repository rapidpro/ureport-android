package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class MultipleChoiceQuestion extends PollQuestion {

    private List<String> choices;

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringList(this.choices);
    }

    public MultipleChoiceQuestion() {
    }

    protected MultipleChoiceQuestion(Parcel in) {
        super(in);
        this.choices = in.createStringArrayList();
    }

    public static final Creator<MultipleChoiceQuestion> CREATOR = new Creator<MultipleChoiceQuestion>() {
        public MultipleChoiceQuestion createFromParcel(Parcel source) {
            return new MultipleChoiceQuestion(source);
        }

        public MultipleChoiceQuestion[] newArray(int size) {
            return new MultipleChoiceQuestion[size];
        }
    };
}
