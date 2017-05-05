package in.ureport.network;

import in.ureport.models.ip.ProxyResponse;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * Created by johncordeiro on 26/10/15.
 */
public interface ProxyApi {

    @GET("/authentication/{country}")
    ProxyResponse getAuthenticationToken(@Header("Authorization") String apiKey
            , @Path("country") String country);

}
