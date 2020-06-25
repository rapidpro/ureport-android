package in.ureport.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import in.ureport.helpers.GsonDateTypeAdapter;
import in.ureport.models.News;
import retrofit2.Call;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class UreportServices {

    private static final String TAG = "UreportServices";

    private final UreportApi service;

    public UreportServices(String endpoint) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new GsonDateTypeAdapter())
                .create();
        service = ServiceFactory.create(UreportApi.class, endpoint, gson);
    }

    public Call<Response<News>> listNews(Integer organization, Integer page) {
        return service.listNews(organization, page);
    }

}
