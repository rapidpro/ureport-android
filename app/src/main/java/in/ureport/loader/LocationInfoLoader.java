package in.ureport.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.ureport.managers.CountryProgramManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import in.ureport.models.holders.LocationInfo;
import in.ureport.models.rapidpro.Boundary;
import in.ureport.network.GeonamesServices;
import in.ureport.network.ProxyApi;
import in.ureport.network.ProxyServices;
import in.ureport.network.RapidProServices;
import in.ureport.network.Response;

/**
 * Created by johncordeiro on 10/09/15.
 */
public class LocationInfoLoader extends AsyncTaskLoader<LocationInfo> {

    private static final String TAG = "StatesLoader";

    private final CountryInfo countryInfo;

    public LocationInfoLoader(Context context, CountryInfo countryInfo) {
        super(context);
        this.countryInfo = countryInfo;
    }

    @Override
    public LocationInfo loadInBackground() {
        try {
            ProxyServices proxyServices = new ProxyServices(getContext());
            ProxyApi.Response response = proxyServices.getAuthenticationTokenByCountry(countryInfo.getIsoAlpha3());

            List<Boundary> boundaries = loadBoundariesByRapidPro(response.token);
            if(boundaries.size() > 1) {
                return getStatesByRapidproResponse(boundaries);
            } else {
                return new LocationInfo(loadGeonamesStates(), null);
            }
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return new LocationInfo(loadGeonamesStates(), null);
    }

    @NonNull
    private List<Boundary> loadBoundariesByRapidPro(String apiToken) {
        CountryProgram countryProgram = CountryProgramManager.getCountryProgramForCode(countryInfo.getIsoAlpha3());
        RapidProServices rapidProServices = new RapidProServices(getContext().getString(countryProgram.getRapidproEndpoint()));

        List<Boundary> boundaries = new ArrayList<>();
        Response<Boundary> response;
        int page = 1;
        do {
            response = rapidProServices.loadBoundaries(apiToken, page);
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
            location.setBoundary(boundary.getBoundary());
            location.setParent(boundary.getParent());
            if(boundary.getLevel() != null && boundary.getLevel() == 1) {
                states.add(location);
            } else if(boundary.getLevel() != null && boundary.getLevel() == 2) {
                districts.add(location);
            }
        }
        return new LocationInfo(states, districts);
    }

    private List<Location> loadGeonamesStates() {
        try {
            GeonamesServices services = new GeonamesServices();

            if (countryInfo != null) {
                return services.getStates(countryInfo.getGeonameId());
            }
        } catch(Exception exception) {
            Log.e(TAG, "loadGeonamesStates ", exception);
        }
        return new ArrayList<>();
    }

    public CountryInfo getCountryInfo() {
        return countryInfo;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

}
