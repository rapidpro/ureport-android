package in.ureport.models;

import java.util.Date;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class Contribution {

    private String key;

    private String content;

    private User author;

    private Date createdDate;

    public Contribution() {
    }

    public Contribution(String content, User author) {
        this.content = content;
        this.author = author;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Contribution{" +
                "key='" + key + '\'' +
                ", content='" + content + '\'' +
                ", author=" + author +
                ", createdDate=" + createdDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contribution that = (Contribution) o;
        return key.equals(that.key);

    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
