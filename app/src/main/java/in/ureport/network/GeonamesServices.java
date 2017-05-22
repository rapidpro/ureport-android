package in.ureport.network;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.ureport.BuildConfig;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class GeonamesServices {

    private static final String ENDPOINT = "http://api.geonames.org";
    private static final String USERNAME = "ureport";

    private final GeonamesApi service;

    public GeonamesServices() {
        Retrofit retrofit = buildRestAdapter();
        service = retrofit.create(GeonamesApi.class);
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

    private Retrofit buildRestAdapter() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES);

        if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(logging);
        }

        return new Retrofit.Builder()
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ENDPOINT)
                .build();
    }

}
