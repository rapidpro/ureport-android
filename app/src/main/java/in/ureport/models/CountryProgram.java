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

    private int organization;

    @StringRes
    private int apiToken;

    private String twitter;

    private String group;

    public CountryProgram(String code, @StyleRes int theme, String name, int organization, @StringRes int apiToken, String twitter, String group) {
        this.code = code;
        this.theme = theme;
        this.name = name;
        this.organization = organization;
        this.apiToken = apiToken;
        this.twitter = twitter;
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

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
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
