package in.ureport.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.ureport.managers.CountryProgramManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import in.ureport.models.holders.LocationInfo;
import in.ureport.models.ip.ProxyResponse;
import in.ureport.network.GeonamesServices;
import in.ureport.network.ProxyServices;
import io.fcmchannel.sdk.core.models.Boundary;
import io.fcmchannel.sdk.core.models.network.ApiResponse;
import io.fcmchannel.sdk.core.network.RestServices;

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
            ProxyResponse response = proxyServices.getAuthenticationTokenByCountry(countryInfo.getIsoAlpha3());

            List<Boundary> boundaries = loadBoundariesByRapidPro(response.getToken());
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
    private List<Boundary> loadBoundariesByRapidPro(String apiToken) throws IOException {
        CountryProgram countryProgram = CountryProgramManager.getCountryProgramForCode(countryInfo.getIsoAlpha3());
        RestServices restServices = new RestServices(getContext().getString(countryProgram.getRapidproEndpoint()), apiToken);

        List<Boundary> boundaries = new ArrayList<>();
        ApiResponse<Boundary> response;
        int page = 1;
        do {
            response = restServices.loadBoundaries(page, true).execute().body();
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
