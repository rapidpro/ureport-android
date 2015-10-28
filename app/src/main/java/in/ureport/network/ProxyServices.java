package in.ureport.network;

import android.content.Context;

import in.ureport.R;
import retrofit.RestAdapter;

/**
 * Created by johncordeiro on 26/10/15.
 */
public class ProxyServices {

    private final String authorization;
    private final ProxyApi proxyApi;

    public ProxyServices(Context context) {
        authorization = context.getString(R.string.proxy_api_key);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(context.getString(R.string.proxy_url))
                .build();

        proxyApi = restAdapter.create(ProxyApi.class);
    }

    public ProxyApi.Response getAuthenticationTokenByCountry(String country) {
        return proxyApi.getAuthenticationToken(authorization, country);
    }
}
