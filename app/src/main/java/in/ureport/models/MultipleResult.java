package in.ureport.models;

import android.os.Parcel;

import java.util.List;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class MultipleResult extends PollResult {

    private List<ItemChoice> results;

    public List<ItemChoice> getResults() {
        return results;
    }

    public void setResults(List<ItemChoice> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(results);
    }

    public MultipleResult() {
        setType(Type.Choices);
    }

    protected MultipleResult(Parcel in) {
        super(in);
        this.results = in.createTypedArrayList(ItemChoice.CREATOR);
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
