package in.ureport;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import in.ureport.managers.CognitoLoginManager;
import in.ureport.managers.UserManager;
import io.fabric.sdk.android.Fabric;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class UreportApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeFabric();
        ActiveAndroid.initialize(this);
        CognitoLoginManager.initialize(this);
    }

    private void initializeFabric() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret));
        Fabric.with(this, new Twitter(authConfig));
    }

    public static boolean validateUserLogin(Context context) {
        return UserManager.validateKeyAction(context);
    }
}
