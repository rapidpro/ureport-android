package in.ureport.models.holders;

import java.util.Locale;

/**
 * Created by ilhasoft on 7/9/15.
 */
public class UserLocale {

    private Locale locale;

    public UserLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserLocale that = (UserLocale) o;
        return locale.getDisplayCountry().equals(that.locale.getDisplayCountry());

    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public int hashCode() {
        return locale.getDisplayCountry().hashCode();
    }

    @Override
    public String toString() {
        return locale.getDisplayCountry();
    }
}
