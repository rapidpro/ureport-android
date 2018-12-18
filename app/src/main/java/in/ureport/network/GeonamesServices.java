package in.ureport.network;

import java.io.IOException;
import java.util.List;

import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class GeonamesServices {

    private static final String ENDPOINT = "http://api.geonames.org";
    private static final String USERNAME = "ureport";

    private final GeonamesApi service;

    public GeonamesServices() {
        service = ServiceFactory.create(GeonamesApi.class, ENDPOINT);
    }

    public List<CountryInfo> getCountryInfo(String country) throws IOException, NullPointerException {
        return service.getCountryInfo(country, USERNAME).execute().body().getGeonames();
    }

    public List<CountryInfo> getCountriesByLanguage(String language) throws IOException, NullPointerException {
        return service.getCountriesByLanguage(language, USERNAME).execute().body().getGeonames();
    }

    public List<Location> getStates(Long geonameId) throws IOException, NullPointerException {
        return service.getStates(geonameId, USERNAME).execute().body().getGeonames();
    }

}
