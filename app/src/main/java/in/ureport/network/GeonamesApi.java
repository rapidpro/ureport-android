package in.ureport.network;

import java.util.List;

import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by johncordeiro on 18/08/15.
 */
public interface GeonamesApi {

    @GET("/countryInfoJSON")
    GeonamesResponse<CountryInfo> getCountryInfo(@Query("country") String country, @Query("username") String username);

    @GET("/countryInfoJSON")
    GeonamesResponse<CountryInfo> getCountriesByLanguage(@Query("lang") String language, @Query("username") String username);

    @GET("/childrenJSON")
    GeonamesResponse<Location> getStates(@Query("geonameId") Long geonameId, @Query("username") String username);

    public class GeonamesResponse<T> {

        private List<T> geonames;

        public List<T> getGeonames() {
            return geonames;
        }

        public void setGeonames(List<T> geonames) {
            this.geonames = geonames;
        }

    }
}
