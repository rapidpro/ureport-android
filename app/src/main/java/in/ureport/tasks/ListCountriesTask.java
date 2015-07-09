package in.ureport.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import in.ureport.models.holders.UserLocale;

/**
 * Created by ilhasoft on 7/9/15.
 */
public class ListCountriesTask extends AsyncTask<Void, Void, List<UserLocale>> {

    @Override
    protected List<UserLocale> doInBackground(Void... voids) {
        List<UserLocale> userLocales = new ArrayList<>();

        Locale [] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            UserLocale userLocale = new UserLocale(locale);

            if(isValidCountry(country) && !userLocales.contains(userLocale)) {
                userLocales.add(userLocale);
            }
        }
        Collections.sort(userLocales, new UserLocaleComparator());
        return userLocales;
    }

    private boolean isValidCountry(String country) {
        return country != null && country.length() > 0;
    }

    private class UserLocaleComparator implements Comparator<UserLocale> {

        @Override
        public int compare(UserLocale userLocale, UserLocale t1) {
            return userLocale.getLocale().getDisplayCountry().compareTo(t1.getLocale().getDisplayCountry());
        }
    }
}
