package in.ureport.network;

import android.content.Context;

import in.ureport.R;
import in.ureport.models.ip.ProxyResponse;

/**
 * Created by johncordeiro on 26/10/15.
 */
public class ProxyServices {

    private final String authorization;
    private final ProxyApi proxyApi;

    public ProxyServices(Context context) {
        authorization = context.getString(R.string.proxy_api_key);
        proxyApi = ServiceFactory.create(ProxyApi.class, context.getString(R.string.proxy_url));
    }

    public ProxyResponse getAuthenticationTokenByCountry(String country) {
        return proxyApi.getAuthenticationToken(authorization, country);
    }

}
