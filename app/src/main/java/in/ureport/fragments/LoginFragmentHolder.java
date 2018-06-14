package in.ureport.fragments;

import android.app.Activity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import in.ureport.managers.FirebaseManager;

public class LoginFragmentHolder {

    private static FacebookCallback<LoginResult> facebookLoginCallback;
    private static Firebase.AuthResultHandler firebaseAuthResultHandler;
    private static Callback<TwitterSession> twitterCallback;

    public static void registerFirebaseAuthResultHandler(Firebase.AuthResultHandler listener) {
        firebaseAuthResultHandler = listener;
    }

    public static void registerFacebookLoginCallback(FacebookCallback<LoginResult> callback) {
        facebookLoginCallback = callback;
    }

    public static void registerTwitterCallback(Callback<TwitterSession> callback) {
        twitterCallback = callback;
    }

    public static void loginWithFacebook(CallbackManager callbackManager) {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookLoginCallback.onSuccess(loginResult);
            }

            @Override
            public void onCancel() {
                facebookLoginCallback.onCancel();
            }

            @Override
            public void onError(FacebookException error) {
                facebookLoginCallback.onError(error);
            }
        });
    }

    public static void authenticateWithFacebook(AccessToken accessToken) {
        FirebaseManager.authenticateWithFacebook(accessToken, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                firebaseAuthResultHandler.onAuthenticated(authData);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                firebaseAuthResultHandler.onAuthenticationError(firebaseError);
            }
        });
    }

    public static void loginWithTwitter(Activity activity, TwitterAuthClient authClient) {
        authClient.authorize(activity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                twitterCallback.success(result);
            }

            @Override
            public void failure(TwitterException e) {
                twitterCallback.failure(e);
            }
        });
    }

    public static void authenticateWithTwitter(TwitterSession session) {
        FirebaseManager.authenticateWithTwitter(session, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                firebaseAuthResultHandler.onAuthenticated(authData);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                firebaseAuthResultHandler.onAuthenticationError(firebaseError);
            }
        });
    }

    public static void authenticateWithGoogle(GoogleApiClient googleApiClient) {
        FirebaseManager.authenticateWithGoogle(googleApiClient, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                firebaseAuthResultHandler.onAuthenticated(authData);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                firebaseAuthResultHandler.onAuthenticationError(firebaseError);
            }
        });
    }

}