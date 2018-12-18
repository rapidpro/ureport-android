package in.ureport.network;

import in.ureport.models.ip.IpResponse;
import retrofit2.Call;

/**
 * Created by john-mac on 7/21/16.
 */
public class IpServices {

    private static final String ENDPOINT = "http://ip-api.com/";

    private final IpApi ipApi;

    public IpServices() {
        ipApi = ServiceFactory.create(IpApi.class, ENDPOINT);
    }

    public Call<IpResponse> getIp() {
        return ipApi.getIpResponse();
    }

}
