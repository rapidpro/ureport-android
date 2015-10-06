package in.ureport.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.ureport.managers.CountryProgramManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import in.ureport.models.holders.LocationInfo;
import in.ureport.models.rapidpro.Boundary;
import in.ureport.network.GeonamesServices;
import in.ureport.network.RapidProServices;
import in.ureport.network.Response;

/**
 * Created by johncordeiro on 10/09/15.
 */
public class LocationInfoLoader extends AsyncTaskLoader<LocationInfo> {

    private static final String TAG = "StatesLoader";

    private final Locale locale;
    private final CountryProgram countryProgram;

    public LocationInfoLoader(Context context, Locale locale) {
        super(context);
        this.locale = locale;
        this.countryProgram = CountryProgramManager.getCountryProgramForCode(locale.getISO3Country());
    }

    @Override
    public LocationInfo loadInBackground() {
        try {
            if(CountryProgramManager.isCountryProgramEnabled(countryProgram)
            && !countryProgram.getCode().equals(CountryProgramManager.COUNTRY_PROGRAM_GLOBAL_CODE)) {
                List<Boundary> boundaries = loadBoundariesByRapidPro(countryProgram);

                if(boundaries.size() > 1) {
                    return getStatesByRapidproResponse(boundaries);
                } else {
                    return new LocationInfo(loadGeonamesStates(), null);
                }
            } else {
                return new LocationInfo(loadGeonamesStates(), null);
            }
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

    @NonNull
    private List<Boundary> loadBoundariesByRapidPro(CountryProgram countryProgram) {
        RapidProServices rapidProServices = new RapidProServices();

        List<Boundary> boundaries = new ArrayList<>();
        Response<Boundary> response;
        int page = 1;
        do {
            response = rapidProServices.loadBoundaries(getContext().getString(countryProgram.getApiToken()), page);
            boundaries.addAll(response.getResults());

            page++;
        } while(response.getNext() != null);
        return boundaries;
    }

    @NonNull
    private LocationInfo getStatesByRapidproResponse(List<Boundary> results) {
        List<Location> states = new ArrayList<>();
        List<Location> districts = new ArrayList<>();

        for (Boundary boundary : results) {
            String name = boundary.getName();
            String toponymName = boundary.getAliases() != null && boundary.getAliases().size() > 0
                    ? boundary.getAliases().get(0) : name;

            Location location = new Location(name, toponymName);
            if(boundary.getLevel() != null && boundary.getLevel() == 1) {
                states.add(location);
            } else if(boundary.getLevel() != null && boundary.getLevel() == 2) {
                districts.add(location);
            }
        }
        return new LocationInfo(states, districts);
    }

    private List<Location> loadGeonamesStates() {
        GeonamesServices services = new GeonamesServices();
        List<CountryInfo> countryInfos = services.getCountryInfo(locale.getCountry());

        if (countryInfos.size() > 0) {
            CountryInfo countryInfo = countryInfos.get(0);
            return services.getStates(countryInfo.getGeonameId());
        }
        return null;
    }

}
