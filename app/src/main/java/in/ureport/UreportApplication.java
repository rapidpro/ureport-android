package in.ureport;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import in.ureport.managers.AmazonServicesManager;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseProxyManager;
import in.ureport.managers.UserManager;
import in.ureport.services.UreportFcmRegistrationService;
import io.fabric.sdk.android.Fabric;
import io.fcmchannel.sdk.FcmClient;
import io.fcmchannel.sdk.UiConfiguration;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class UreportApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(BuildConfig.DEBUG ? Log.DEBUG : Log.ASSERT))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret)))
                .debug(BuildConfig.DEBUG)
                .build();
        Twitter.initialize(config);

        AnalyticsTracker.initialize(this);
        UserManager.init(this);
        FirebaseProxyManager.init(this);
        ActiveAndroid.initialize(this);
        AmazonServicesManager.init(this);
        initializeFcmClient();
    }

    private void initializeFcmClient() {
        if (UserManager.isUserLoggedIn()) {
            UserManager.initializeFcmClient(CountryProgramManager.getCurrentCountryProgram());
        } else {
            FcmClient.initialize(new FcmClient.Builder(this)
                    .setRegistrationServiceClass(UreportFcmRegistrationService.class)
                    .setUiConfiguration(new UiConfiguration()
                            .setPermissionMessage(getString(R.string.message_fcm_floating_permission))));
        }
    }

}
