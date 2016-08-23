package in.ureport.network;

import in.ureport.models.ip.IpResponse;
import retrofit.Callback;
import retrofit.http.POST;

/**
 * Created by john-mac on 7/21/16.
 */
public interface IpApi {

    @POST("/json")
    void getIpResponse(Callback<IpResponse> callback);

}
