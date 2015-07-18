package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by johncordeiro on 18/07/15.
 */
public abstract class PollResult implements Parcelable {

    private PollQuestion question;

    private Date date;

    private int responded;

    private int polled;

    public PollQuestion getQuestion() {
        return question;
    }

    public void setQuestion(PollQuestion question) {
        this.question = question;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getResponded() {
        return responded;
    }

    public void setResponded(int responded) {
        this.responded = responded;
    }

    public int getPolled() {
        return polled;
    }

    public void setPolled(int polled) {
        this.polled = polled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.question, 0);
        dest.writeLong(date != null ? date.getTime() : -1);
        dest.writeInt(this.responded);
        dest.writeInt(this.polled);
    }

    public PollResult() {
    }

    protected PollResult(Parcel in) {
        this.question = in.readParcelable(PollQuestion.class.getClassLoader());
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.responded = in.readInt();
        this.polled = in.readInt();
    }

}
