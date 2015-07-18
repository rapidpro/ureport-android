package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johncordeiro on 7/17/15.
 */
public abstract class PollQuestion implements Parcelable {

    private String question;

    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.question);
        dest.writeString(this.answer);
    }

    public PollQuestion() {
    }

    protected PollQuestion(Parcel in) {
        this.question = in.readString();
        this.answer = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PollQuestion that = (PollQuestion) o;

        return question.equals(that.question);

    }

    @Override
    public int hashCode() {
        return question.hashCode();
    }
}
