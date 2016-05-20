package in.ureport.models;

import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by johncordeiro on 7/23/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryProgram {

    private String code;

    @StyleRes
    private int theme;

    @StringRes
    private int channel;

    private String name;

    private int organization;

    @StringRes
    private int rapidproEndpoint;

    @StringRes
    private int ureportEndpoint;

    private String twitter;

    private String facebook;

    private String group;

    public CountryProgram(String code, @StyleRes int theme, @StringRes int channel
            , String name, int organization, int rapidproEndpoint, int ureportEndpoint, String twitter, String facebook, String group) {
        this.code = code;
        this.theme = theme;
        this.channel = channel;
        this.name = name;
        this.organization = organization;
        this.rapidproEndpoint = rapidproEndpoint;
        this.ureportEndpoint = ureportEndpoint;
        this.twitter = twitter;
        this.facebook = facebook;
        this.group = group;
    }

    public CountryProgram(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public CountryProgram(String code) {
        this.code = code;
    }

    @StyleRes
    public int getTheme() {
        return theme;
    }

    public void setTheme(@StyleRes int theme) {
        this.theme = theme;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrganization() {
        return organization;
    }

    public void setOrganization(int organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRapidproEndpoint() {
        return rapidproEndpoint;
    }

    public void setRapidproEndpoint(int rapidproEndpoint) {
        this.rapidproEndpoint = rapidproEndpoint;
    }

    public int getUreportEndpoint() {
        return ureportEndpoint;
    }

    public void setUreportEndpoint(int ureportEndpoint) {
        this.ureportEndpoint = ureportEndpoint;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryProgram that = (CountryProgram) o;
        return code.equalsIgnoreCase(that.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
