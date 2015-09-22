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

    private String name;

    @StringRes
    private int apiToken;

    @StringRes
    private int channel;

    private String twitter;

    public CountryProgram(String code, @StyleRes int theme, String name, @StringRes int apiToken, @StringRes int channel, String twitter) {
        this.code = code;
        this.theme = theme;
        this.name = name;
        this.apiToken = apiToken;
        this.channel = channel;
        this.twitter = twitter;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getApiToken() {
        return apiToken;
    }

    public void setApiToken(int apiToken) {
        this.apiToken = apiToken;
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

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
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
