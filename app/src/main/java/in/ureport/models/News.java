package in.ureport.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class News implements Parcelable {

    private String title;

    private String content;

    private String author;

    private @DrawableRes int cover;

    public News(String title, String author, String content, @DrawableRes int cover) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.author);
        dest.writeInt(this.cover);
    }

    protected News(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.author = in.readString();
        this.cover = in.readInt();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
