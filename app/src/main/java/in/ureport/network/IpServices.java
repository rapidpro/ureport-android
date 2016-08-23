package in.ureport.network;

import com.google.gson.GsonBuilder;

import in.ureport.BuildConfig;
import in.ureport.models.ip.IpResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by john-mac on 7/21/16.
 */
public class IpServices {

    public static final String ENDPOINT = "http://ip-api.com/";

    private final IpApi ipApi;

    public IpServices() {
        RestAdapter restAdapter = buildRestAdapter();
        if(BuildConfig.DEBUG)
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        ipApi = restAdapter.create(IpApi.class);
    }

    public void getIp(Callback<IpResponse> callback) {
        ipApi.getIpResponse(callback);
    }

    private RestAdapter buildRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(new GsonBuilder().create()))
                .build();
    }

}
