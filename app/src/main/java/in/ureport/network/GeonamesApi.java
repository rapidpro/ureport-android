package in.ureport.network;

import java.util.List;

import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by johncordeiro on 18/08/15.
 */
public interface GeonamesApi {

    @GET("/countryInfoJSON")
    Call<GeonamesResponse<CountryInfo>> getCountryInfo(@Query("country") String country, @Query("username") String username);

    @GET("/countryInfoJSON")
    Call<GeonamesResponse<CountryInfo>> getCountriesByLanguage(@Query("lang") String language, @Query("username") String username);

    @GET("/childrenJSON")
    Call<GeonamesResponse<Location>> getStates(@Query("geonameId") Long geonameId, @Query("username") String username);

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
