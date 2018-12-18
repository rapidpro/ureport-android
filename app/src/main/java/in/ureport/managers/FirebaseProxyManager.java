package in.ureport.managers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import in.ureport.models.CountryProgram;
import in.ureport.models.ip.IpResponse;
import in.ureport.network.IpServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by john-mac on 7/21/16.
 */
public class FirebaseProxyManager {

    private static final String TAG = "FirebaseProxyManager";

    private static final String COUNTRY_SYRIA = "SY";
    private static final String COUNTRY_LEBANON = "LB";

    /** List of countries that need proxy to connect with Firebase */
    private final static Map<String, CountryProgram> proxyCountries;

    private static boolean proxyEnabled = false;

    static {
        proxyCountries = new HashMap<>();
        proxyCountries.put(COUNTRY_SYRIA, new CountryProgram("SYR"));
        proxyCountries.put(COUNTRY_LEBANON, new CountryProgram("LBN"));
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
        ipServices.getIp().enqueue(new Callback<IpResponse>() {
            @Override
            public void onResponse(Call<IpResponse> call, Response<IpResponse> response) {
                IpResponse body = response.body();
                if (body != null && body.getCountryCode() != null) {
                    proxyEnabled = proxyCountries.containsKey(body.getCountryCode());
                }
                updateFirebaseManager(context);
            }

            @Override
            public void onFailure(Call<IpResponse> call, Throwable t) {
                Log.e(TAG, "failure: ", t);
                proxyEnabled = false;
                updateFirebaseManager(context);
            }
        });
    }

}
