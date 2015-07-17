package in.ureport.models;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class Contribution {

    private String contribution;

    private User author;

    public Contribution() {
    }

    public Contribution(String contribution, User author) {
        this.contribution = contribution;
        this.author = author;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
