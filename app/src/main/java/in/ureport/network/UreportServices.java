package in.ureport.network;

import in.ureport.models.News;
import retrofit2.Call;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class UreportServices {

    private static final String TAG = "UreportServices";

    private final UreportApi service;

    public UreportServices(String endpoint) {
        service = ServiceFactory.create(UreportApi.class, endpoint);
    }

    public Call<Response<News>> listNews(Integer organization, Integer page) {
        return service.listNews(organization, page);
    }

}
