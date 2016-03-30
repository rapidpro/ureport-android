package in.ureport.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;

import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.managers.UserSocialAuthBuilder;
import in.ureport.models.User;
import in.ureport.managers.FirebaseManager;

/**
 * Created by johncordeiro on 7/7/15.
 */
public class LoginFragment extends Fragment implements Firebase.AuthResultHandler {

    public static final String [] FACEBOOK_PERMISSIONS = { "email", "user_birthday" };
    public static final int ERROR_RESOLUTION_REQUEST_CODE = 300;
    public static final int REQUEST_CODE_GET_ACCOUNTS_PERMISSION = 101;

    private LoginListener loginListener;
    private StatusBarDesigner statusBarDesigner;
    private UserSocialAuthBuilder userSocialAuthBuilder;

    private CallbackManager callbackManager;
    private TwitterAuthClient twitterAuthClient;
    private GoogleApiClient googleApiClient;

    private ProgressDialog loadUserDialog;

    private boolean resolvingGoogleSignin = false;
    private boolean shouldResolveErrors = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        onActivityResultGoogleSignin(requestCode, resultCode);
    }

    private void onActivityResultGoogleSignin(int requestCode, int resultCode) {
        if (requestCode == ERROR_RESOLUTION_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) shouldResolveErrors = false;
            resolvingGoogleSignin = false;
            googleApiClient.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CODE_GET_ACCOUNTS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loginWithGooglePlus();
                } else {
                    Toast.makeText(getContext(),  R.string.error_message_permission, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setupObjects() {
        FacebookSdk.sdkInitialize(getActivity());

        statusBarDesigner = new StatusBarDesigner();
        userSocialAuthBuilder = new UserSocialAuthBuilder();
        callbackManager = CallbackManager.Factory.create();
        twitterAuthClient = new TwitterAuthClient();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(googleConnectionCallbacks)
                .addOnConnectionFailedListener(googleConnectionFailedListener)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        statusBarDesigner.setStatusBarColorById(getActivity(), R.color.yellow);
    }

    private void setupView(View view) {
        TextView skipLogin = (TextView) view.findViewById(R.id.skipLogin);
        skipLogin.setOnClickListener(onSkipLoginClickListener);

        Button loginWithCredentials = (Button) view.findViewById(R.id.loginWithCredentials);
        loginWithCredentials.setOnClickListener(onLoginWithCredentialsClickListener);

        Button loginWithTwitter = (Button) view.findViewById(R.id.loginWithTwitter);
        loginWithTwitter.setOnClickListener(onTwitterLoginClickListener);

        Button loginWithGoogle = (Button) view.findViewById(R.id.loginWithGoogle);
        loginWithGoogle.setOnClickListener(onGoogleLoginClickListener);

        Button loginWithFacebook = (Button) view.findViewById(R.id.loginWithFacebook);
        loginWithFacebook.setOnClickListener(onFacebookLoginClickListener);

        TextView signUp = (TextView) view.findViewById(R.id.signUp);
        signUp.setOnClickListener(onSignUpClickListener);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if(activity instanceof LoginListener) {
            loginListener = (LoginListener)activity;
        }
    }

    private View.OnClickListener onLoginWithCredentialsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(loginListener != null) {
                loginListener.onLoginWithCredentials();
            }
        }
    };

    private View.OnClickListener onSignUpClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (loginListener != null) {
                loginListener.onSignUp();
            }
        }
    };

    private FacebookCallback<LoginResult> onFacebookLoginCallback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {
            loadUserDialog = showLoadUserProgress();
            FirebaseManager.authenticateWithFacebook(loginResult.getAccessToken(), LoginFragment.this);
        }

        @Override
        public void onCancel() {}

        @Override
        public void onError(FacebookException exception) {
            showLoginErrorAlert();
        }
    };

    @NonNull
    private ProgressDialog showLoadUserProgress() {
        return ProgressDialog.show(getActivity()
                    , null, getString(R.string.login_load_user_message), true, false);
    }

    private void showLoginErrorAlert() {
        Toast.makeText(getActivity(), R.string.login_error, Toast.LENGTH_LONG).show();
    }

    private View.OnClickListener onTwitterLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            twitterAuthClient.authorize(getActivity(), twitterLoginCallback);
        }
    };

    private GoogleApiClient.ConnectionCallbacks googleConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            shouldResolveErrors = false;
            FirebaseManager.authenticateWithGoogle(googleApiClient, LoginFragment.this);
        }

        @Override
        public void onConnectionSuspended(int connectionSuspended) {}
    };

    private GoogleApiClient.OnConnectionFailedListener googleConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            if (!resolvingGoogleSignin && shouldResolveErrors) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(getActivity(), ERROR_RESOLUTION_REQUEST_CODE);
                        resolvingGoogleSignin = true;
                    } catch (IntentSender.SendIntentException exception) {
                        resolvingGoogleSignin = false;
                        googleApiClient.connect();
                    }
                } else {
                    loadUserDialog.dismiss();
                    showLoginErrorAlert();
                }
            }
        }
    };

    private Callback<TwitterSession> twitterLoginCallback = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {
            loadUserDialog = showLoadUserProgress();
            FirebaseManager.authenticateWithTwitter(result.data, LoginFragment.this);
        }

        @Override
        public void failure(TwitterException exception) {
            showLoginErrorAlert();
        }
    };

    private View.OnClickListener onFacebookLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, onFacebookLoginCallback);
            loginManager.logInWithReadPermissions(LoginFragment.this, Arrays.asList(FACEBOOK_PERMISSIONS));
        }
    };

    private View.OnClickListener onGoogleLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hasWriteContactsPermission = getActivity().checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS);
                if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] { android.Manifest.permission.GET_ACCOUNTS }
                            , REQUEST_CODE_GET_ACCOUNTS_PERMISSION);
                }
            } else {
                loginWithGooglePlus();
            }
        }
    };

    private void loginWithGooglePlus() {
        loadUserDialog = showLoadUserProgress();

        if(!googleApiClient.isConnected()) {
            googleApiClient.connect();
        } else {
            googleConnectionCallbacks.onConnected(null);
        }
    }

    private View.OnClickListener onSkipLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (loginListener != null)
                loginListener.onSkipLogin();
        }
    };

    @Override
    public void onAuthenticated(AuthData authData) {
        loadUserDialog.dismiss();

        User user = userSocialAuthBuilder.build(authData);
        if(loginListener != null) {
            loginListener.onLoginWithSocialNetwork(user);
        }
    }

    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        loadUserDialog.dismiss();
        showLoginErrorAlert();
    }

    public interface LoginListener {
        void onLoginWithSocialNetwork(User user);
        void onLoginWithCredentials();
        void onSkipLogin();
        void onSignUp();
        void onUserReady(User user, boolean newUser);
        void onForgotPassword();
        void onPasswordReset();
    }
}
