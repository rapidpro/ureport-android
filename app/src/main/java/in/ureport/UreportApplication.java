package in.ureport;

import android.support.multidex.MultiDexApplication;

import com.activeandroid.ActiveAndroid;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.ArrayList;
import java.util.List;

import in.ureport.managers.AmazonServicesManager;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseProxyManager;
import in.ureport.managers.UserManager;
import in.ureport.services.UreportFcmRegistrationService;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.rapidpro.sdk.FcmClient;
import io.rapidpro.sdk.UiConfiguration;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class UreportApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        AnalyticsTracker.initialize(this);
        UserManager.init(this);
        FirebaseProxyManager.init(this);
        initializeFabric();
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

    private void initializeFabric() {
        final Twitter twitter = new Twitter(new TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret)));
        if (BuildConfig.DEBUG) {
            Fabric.with(this, twitter);
        } else {
            Fabric.with(this, twitter, new Crashlytics());
        }
    }

}
