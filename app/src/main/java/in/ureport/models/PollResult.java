package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johncordeiro on 18/07/15.
 */
public abstract class PollResult implements Parcelable {

    public enum Type {
        Choices,
        Keywords
    }

    private Integer id;

    private String date;

    private String responded;

    private String polled;

    private String title;

    private Type type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
        dest.writeValue(this.id);
        dest.writeString(this.date);
        dest.writeString(this.responded);
        dest.writeString(this.polled);
        dest.writeString(this.title);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    public PollResult() {
    }

    protected PollResult(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.date = in.readString();
        this.responded = in.readString();
        this.polled = in.readString();
        this.title = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : PollResult.Type.values()[tmpType];
    }
}
