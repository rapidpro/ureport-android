package in.ureport.models;

import android.os.Parcel;

import java.util.List;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class WordsResult extends PollResult {

    private List<String> results;

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringList(this.results);
    }

    public WordsResult() {
    }

    protected WordsResult(Parcel in) {
        super(in);
        this.results = in.createStringArrayList();
    }

    public static final Creator<WordsResult> CREATOR = new Creator<WordsResult>() {
        public WordsResult createFromParcel(Parcel source) {
            return new WordsResult(source);
        }

        public WordsResult[] newArray(int size) {
            return new WordsResult[size];
        }
    };
}
