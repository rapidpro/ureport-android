package in.ureport.network;

import in.ureport.models.ip.IpResponse;
import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by john-mac on 7/21/16.
 */
public interface IpApi {

    @POST("/json")
    Call<IpResponse> getIpResponse();

}
