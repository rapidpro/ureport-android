package in.ureport.network;

import java.util.List;

import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import retrofit.RestAdapter;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class GeonamesServices {

    private static final String ENDPOINT = "http://api.geonames.org";
    private static final String USERNAME = "ureport";

    private final GeonamesApi service;

    public GeonamesServices() {
        RestAdapter restAdapter = buildRestAdapter();
        service = restAdapter.create(GeonamesApi.class);
    }

    public List<CountryInfo> getCountryInfo(String country) {
        return service.getCountryInfo(country, USERNAME).getGeonames();
    }

    public List<Location> getStates(Long geonameId) {
        return service.getStates(geonameId, USERNAME).getGeonames();
    }

    private RestAdapter buildRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
    }

}
