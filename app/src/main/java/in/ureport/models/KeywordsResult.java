package in.ureport.models;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by johncordeiro on 18/07/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeywordsResult extends PollResult {

    private List<ItemKeyword> results;

    public List<ItemKeyword> getResults() {
        return results;
    }

    public void setResults(List<ItemKeyword> results) {
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

    public KeywordsResult() {
        setType(Type.Keywords);
    }

    protected KeywordsResult(Parcel in) {
        super(in);
        this.results = in.createTypedArrayList(ItemKeyword.CREATOR);
    }

    public static final Creator<KeywordsResult> CREATOR = new Creator<KeywordsResult>() {
        public KeywordsResult createFromParcel(Parcel source) {
            return new KeywordsResult(source);
        }

        public KeywordsResult[] newArray(int size) {
            return new KeywordsResult[size];
        }
    };
}
