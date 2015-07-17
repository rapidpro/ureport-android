package in.ureport.models;

import java.util.Date;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class Poll {

    private String description;

    private Date expirationDate;

    private int responseRate;

    private int responded;

    private int polled;

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
}
