package in.ureport.network;

import in.ureport.models.News;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by johncordeiro on 18/08/15.
 */
public interface UreportApi {

    @GET("/stories/org/{org}/")
    Call<Response<News>> listNews(@Path("org") int organization, @Query("page") int page);

}
