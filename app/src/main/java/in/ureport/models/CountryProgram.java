package in.ureport.models;

import android.support.annotation.StyleRes;

/**
 * Created by johncordeiro on 7/23/15.
 */
public class CountryProgram {

    private String code;

    @StyleRes
    private int theme;

    private String name;

    public CountryProgram(String code, int theme, String name) {
        this.code = code;
        this.theme = theme;
        this.name = name;
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

    @Override
    public String toString() {
        return name;
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
