package in.ureport.network;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * Created by johncordeiro on 26/10/15.
 */
public interface ProxyApi {

    @GET("/authentication/{country}")
    ProxyApi.Response getAuthenticationToken(@Header("Authorization") String apiKey
            , @Path("country") String country);

    class Response {
        public String token;
    }

}
