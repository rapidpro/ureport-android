package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class Poll implements Parcelable {

    private String description;

    private Date expirationDate;

    private int responseRate;

    private int responded;

    private int polled;

    private List<? extends PollQuestion> questions;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getResponseRate() {
        return responseRate;
    }

    public void setResponseRate(int responseRate) {
        this.responseRate = responseRate;
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

    public List<? extends PollQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<? extends PollQuestion> questions) {
        this.questions = questions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeLong(expirationDate != null ? expirationDate.getTime() : -1);
        dest.writeInt(this.responseRate);
        dest.writeInt(this.responded);
        dest.writeInt(this.polled);
        dest.writeList(this.questions);
    }

    public Poll() {
    }

    protected Poll(Parcel in) {
        this.description = in.readString();
        long tmpExpirationDate = in.readLong();
        this.expirationDate = tmpExpirationDate == -1 ? null : new Date(tmpExpirationDate);
        this.responseRate = in.readInt();
        this.responded = in.readInt();
        this.polled = in.readInt();
        this.questions = new ArrayList<>();
        in.readList(this.questions, PollQuestion.class.getClassLoader());
    }

    public static final Creator<Poll> CREATOR = new Creator<Poll>() {
        public Poll createFromParcel(Parcel source) {
            return new Poll(source);
        }

        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };
}
