package in.ureport.network;

import in.ureport.models.News;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by johncordeiro on 18/08/15.
 */
public interface UreportApi {

    @GET("/stories/org/{org}/")
    void listNews(@Path("org") int organization, @Query("page") int page, Callback<Response<News>> callback);

}
