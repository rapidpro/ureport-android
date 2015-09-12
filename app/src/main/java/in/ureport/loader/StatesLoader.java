package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.State;
import in.ureport.network.GeonamesServices;

/**
 * Created by johncordeiro on 10/09/15.
 */
public class StatesLoader extends AsyncTaskLoader<List<State>> {

    private static final String TAG = "StatesLoader";

    private final Locale locale;

    public StatesLoader(Context context, Locale locale) {
        super(context);
        this.locale = locale;
    }

    @Override
    public List<State> loadInBackground() {
        try {
            GeonamesServices services = new GeonamesServices();
            List<CountryInfo> countryInfos = services.getCountryInfo(locale.getCountry());

            if (countryInfos.size() > 0) {
                CountryInfo countryInfo = countryInfos.get(0);
                return services.getStates(countryInfo.getGeonameId());
            }
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

}
