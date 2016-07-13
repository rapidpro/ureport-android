package in.ureport.managers;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.firebase.client.Config;
import com.firebase.client.Firebase;
import com.firebase.client.core.RepoManager;
import com.firebase.client.utilities.ParsedUrl;
import com.firebase.client.utilities.Utilities;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.ureport.R;
import in.ureport.models.User;
import in.ureport.tasks.GetGoogleAuthTokenTask;

/**
 * Created by johncordeiro on 15/08/15.
 */
public class FirebaseManager {

    /** List of countries that need proxy to connect with Firebase */
    private final static List<String> proxyCountries = Collections.singletonList("SYR");

    private static Firebase reference;
    private static Context context;

    public static void init(Context context) {
        Firebase.setAndroidContext(context);

        boolean needsProxy = needsProxy(context);

        if (needsProxy) {
            Config config = new Config();
            config.setAuthenticationServer(context.getString(R.string.firebase_proxy_auth));
            Firebase.setDefaultConfig(config);
        }

        if(reference == null) {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
            FirebaseManager.context = context;

            String appUrl = needsProxy ? context.getString(R.string.firebase_proxy_database) :
                    context.getString(R.string.firebase_app);
            reference = getInstanceWithCustomName(appUrl, context.getString(R.string.firebase_app_name));
        }
    }

    private static boolean needsProxy(Context context) {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        String iso3Country = currentLocale.getISO3Country();

        return !TextUtils.isEmpty(iso3Country) && proxyCountries.contains(iso3Country);
    }

    private static Firebase getInstanceWithCustomName(String url, String name) {
        ParsedUrl parsedUrl = Utilities.parseUrl(url);
        parsedUrl.repoInfo.namespace = name;
        return new Firebase(RepoManager.getRepo(Firebase.getDefaultConfig(), parsedUrl.repoInfo), parsedUrl.path);
    }

    public static void logout() {
        reference.unauth();
    }

    public static void changePassword(User user, String oldPassword, String newPassword, Firebase.ResultHandler resultHanlder) {
        reference.changePassword(user.getEmail(), oldPassword, newPassword, resultHanlder);
    }

    public static void authenticateWithGoogle(GoogleApiClient client, final Firebase.AuthResultHandler handler) {
        GetGoogleAuthTokenTask getGoogleAuthTokenTask = new GetGoogleAuthTokenTask() {
            @Override
            protected void onPostExecute(String token) {
                super.onPostExecute(token);
                reference.authWithOAuthToken("google", token, handler);
            }
        };
        getGoogleAuthTokenTask.execute(client);
    }

    public static void authenticateWithFacebook(AccessToken token, final Firebase.AuthResultHandler handler) {
        reference.authWithOAuthToken("facebook", token.getToken(), handler);
    }

    public static void authenticateWithTwitter(TwitterSession session, final Firebase.AuthResultHandler handler) {
        Map<String, String> options = new HashMap<>();
        options.put("oauth_token", session.getAuthToken().token);
        options.put("oauth_token_secret", session.getAuthToken().secret);
        options.put("user_id", String.valueOf(session.getUserId()));

        reference.authWithOAuthToken("twitter", options, handler);
    }

    public static void authorizeCode(String code) {
        Firebase authorization = reference.child("backend_authorization").child(code);
        authorization.child("checked").setValue(true);
        authorization.child("user").setValue(UserManager.getUserId());
    }

    public static Firebase getReference() {
        return reference;
    }
}
