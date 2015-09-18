package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by johncordeiro on 7/16/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Poll implements Parcelable {

    private String key;

    private String title;

    private String flow_uuid;

    private String expiration_date;

    private PollCategory category;

    private String percent_rate;

    private String responded;

    private String polled;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date) {
        this.expiration_date = expiration_date;
    }

    public String getFlow_uuid() {
        return flow_uuid;
    }

    public void setFlow_uuid(String flow_uuid) {
        this.flow_uuid = flow_uuid;
    }

    public PollCategory getCategory() {
        return category;
    }

    public void setCategory(PollCategory category) {
        this.category = category;
    }

    public String getPercent_rate() {
        return percent_rate;
    }

    public void setPercent_rate(String percent_rate) {
        this.percent_rate = percent_rate;
    }

    public String getResponded() {
        return responded;
    }

    public void setResponded(String responded) {
        this.responded = responded;
    }

    public String getPolled() {
        return polled;
    }

    public void setPolled(String polled) {
        this.polled = polled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.title);
        dest.writeString(this.flow_uuid);
        dest.writeString(this.expiration_date);
        dest.writeParcelable(this.category, 0);
        dest.writeString(this.percent_rate);
        dest.writeString(this.responded);
        dest.writeString(this.polled);
    }

    public Poll() {
    }

    protected Poll(Parcel in) {
        this.key = in.readString();
        this.title = in.readString();
        this.flow_uuid = in.readString();
        this.expiration_date = in.readString();
        this.category = in.readParcelable(PollCategory.class.getClassLoader());
        this.percent_rate = in.readString();
        this.responded = in.readString();
        this.polled = in.readString();
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
