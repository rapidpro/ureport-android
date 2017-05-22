package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

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

    private static final String TAG = "CountryListLoader";

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
            Collections.sort(countries, new CountryInfoComparator());
        } catch(Exception exception) {
            Log.e(TAG, "loadInBackground: ", exception);
            countries = new ArrayList<>();
        }
        return countries;
    }

    private class CountryInfoComparator implements Comparator<CountryInfo> {
        @Override
        public int compare(CountryInfo countryInfo, CountryInfo t1) {
            return countryInfo.getCountryName().compareTo(t1.getCountryName());
        }
    }

}
