package in.ureport.managers;

import android.content.Context;

import com.facebook.AccessToken;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.HashMap;
import java.util.Map;

import in.ureport.R;
import in.ureport.models.User;
import in.ureport.tasks.GetGoogleAuthTokenTask;

/**
 * Created by johncordeiro on 15/08/15.
 */
public class FirebaseManager {

    private static Firebase reference;
    private static Context context;

    public static void init(Context context) {
        Firebase.setAndroidContext(context);

        if(reference == null) {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
            FirebaseManager.context = context;
            reference = new Firebase(context.getString(R.string.firebase_app));
        }
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
