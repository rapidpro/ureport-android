package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import in.ureport.models.geonames.CountryInfo;
import in.ureport.network.GeonamesServices;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class CountryListLoader extends AsyncTaskLoader<List<CountryInfo>> {

    public CountryListLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<CountryInfo> loadInBackground() {
        List<CountryInfo> countries;

        try {
            GeonamesServices geonamesServices = new GeonamesServices();
            countries = geonamesServices.getCountriesByLanguage(Locale.getDefault().getLanguage());
        } catch(Exception exception) {
            countries = new ArrayList<>();
        }

        Collections.sort(countries, new CountryInfoComparator());
        return countries;
    }

    private class CountryInfoComparator implements Comparator<CountryInfo> {
        @Override
        public int compare(CountryInfo countryInfo, CountryInfo t1) {
            return countryInfo.getCountryName().compareTo(t1.getCountryName());
        }
    }

}
