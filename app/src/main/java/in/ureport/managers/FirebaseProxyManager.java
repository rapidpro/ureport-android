package in.ureport.managers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import in.ureport.models.CountryProgram;
import in.ureport.models.ip.IpResponse;
import in.ureport.network.IpServices;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by john-mac on 7/21/16.
 */
public class FirebaseProxyManager {

    private static final String TAG = "FirebaseProxyManager";

    private static final String COUNTRY_SYRIA = "SY";

    /** List of countries that need proxy to connect with Firebase */
    private final static Map<String, CountryProgram> proxyCountries;

    private static boolean proxyEnabled = false;

    static {
        proxyCountries = new HashMap<>();
        proxyCountries.put(COUNTRY_SYRIA, new CountryProgram("SYR"));
    }

    public static void init(Context context) {
        if (TextUtils.isEmpty(UserManager.getCountryCode())) {
            requestIp(context);
        } else {
            proxyEnabled = proxyCountries.values().contains(new CountryProgram(UserManager.getCountryCode()));
            updateFirebaseManager(context);
        }
    }

    private static void updateFirebaseManager(Context context) {
        FirebaseManager.init(context, proxyEnabled);
    }

    private static void requestIp(final Context context) {
        IpServices ipServices = new IpServices();
        ipServices.getIp(new Callback<IpResponse>() {
            @Override
            public void success(IpResponse ipResponse, Response response) {
                if (ipResponse.getCountryCode() != null) {
                    proxyEnabled = proxyCountries.containsKey(ipResponse.getCountryCode());
                }
                updateFirebaseManager(context);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failure: ", error);
                proxyEnabled = false;
                updateFirebaseManager(context);
            }
        });
    }

}
