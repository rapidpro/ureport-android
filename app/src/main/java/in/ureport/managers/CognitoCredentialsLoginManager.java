package in.ureport.managers;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;

import java.util.HashMap;
import java.util.Map;

import in.ureport.R;
import in.ureport.listener.OnTaskFinishedListener;
import in.ureport.tasks.GetGoogleAuthTokenTask;

/**
 * Created by johncordeiro on 11/08/15.
 */
public class CognitoCredentialsLoginManager {

    private static final String TAG = "CognitoLoginManager";

    private static CognitoCachingCredentialsProvider credentialsProvider;
    private static CognitoSyncManager syncClient;

    private static Context context;

    public static CognitoCachingCredentialsProvider initialize(Context context) {
        CognitoCredentialsLoginManager.context = context;
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                context.getString(R.string.identity_pool_id),
                Regions.US_EAST_1);

        syncClient = new CognitoSyncManager(context, Regions.US_EAST_1, credentialsProvider);
        return credentialsProvider;
    }

    public static CognitoCachingCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public static void logout() {
        logoutSocialNetworks();
        clearCredentials();
    }

    private static void clearCredentials() {
        syncClient.wipeData();

        credentialsProvider.clear();
        credentialsProvider.clearCredentials();
    }

    private static void logoutSocialNetworks() {
        logoutFacebook();
        logoutTwitter();
        logoutGoogle();
    }

    private static void logoutGoogle() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(googleApiClient);
        }
    }

    private static void logoutTwitter() {
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
    }

    private static void logoutFacebook() {
        LoginManager.getInstance().logOut();
    }

    public static void setFacebookLogin(AccessToken token) {
        clearCredentials();

        Map<String, String> logins = new HashMap<>();
        logins.put("graph.facebook.com", token.getToken());
        credentialsProvider.setLogins(logins);
    }

    public static void setTwitterLogin(TwitterAuthToken authToken) {
        clearCredentials();

        String value = authToken.token + ";" + authToken.secret;
        Map<String, String> logins = new HashMap<>();
        logins.put("api.twitter.com", value);
        credentialsProvider.setLogins(logins);
    }

    public static void setGoogleLogin(GoogleApiClient googleApiClient, final OnTaskFinishedListener onTaskFinishedListener) {
        clearCredentials();
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        GetGoogleAuthTokenTask getGoogleAuthTokenTask = new GetGoogleAuthTokenTask(context) {
            @Override
            protected void onPostExecute(String token) {
                Map<String, String> logins = new HashMap<>();
                logins.put("accounts.google.com", token);
                credentialsProvider.setLogins(logins);

                if(onTaskFinishedListener != null) onTaskFinishedListener.onTaskFinished();
            }
        };
        getGoogleAuthTokenTask.execute(googleApiClient);
    }

}
