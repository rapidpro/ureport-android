package in.ureport.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;

import in.ureport.BuildConfig;
import in.ureport.helpers.GsonDateDeserializer;
import in.ureport.models.News;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class UreportServices {

    private static final String TAG = "UreportServices";

    private static final String ENDPOINT = "http://ureport.in/api/v1";

    private final UreportApi service;

    public UreportServices() {
        RestAdapter restAdapter = buildRestAdapter();
        if(BuildConfig.DEBUG) restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        service = restAdapter.create(UreportApi.class);
    }

    private RestAdapter buildRestAdapter() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new GsonDateDeserializer())
                .create();

        return new RestAdapter.Builder()
                    .setEndpoint(ENDPOINT)
                    .setConverter(new GsonConverter(gson))
                    .build();
    }

    public void listNews(Integer organization, Integer page, Callback<Response<News>> callback) {
        service.listNews(organization, page, callback);
    }

}
