package in.ureport.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import in.ureport.BuildConfig;
import in.ureport.helpers.GsonDateTypeAdapter;
import in.ureport.models.News;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class UreportServices {

    private static final String TAG = "UreportServices";

    private final String endpoint;
    private final UreportApi service;

    public UreportServices(String endpoint) {
        this.endpoint = endpoint;
        RestAdapter restAdapter = buildRestAdapter();
        if(BuildConfig.DEBUG) restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        service = restAdapter.create(UreportApi.class);
    }

    private RestAdapter buildRestAdapter() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new GsonDateTypeAdapter())
                .create();

        return new RestAdapter.Builder()
                    .setEndpoint(endpoint)
                    .setConverter(new GsonConverter(gson))
                    .build();
    }

    public void listNews(Integer organization, Integer page, Callback<Response<News>> callback) {
        service.listNews(organization, page, callback);
    }

}
