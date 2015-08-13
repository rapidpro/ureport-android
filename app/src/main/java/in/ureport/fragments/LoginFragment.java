package in.ureport.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;

import java.util.Arrays;

import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.listener.OnTaskFinishedListener;
import in.ureport.managers.CognitoCredentialsLoginManager;
import in.ureport.managers.UserSocialNetworkBuilder;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/7/15.
 */
public class LoginFragment extends Fragment implements FacebookCallback<LoginResult> {

    private static final String TAG = "LoginFragment";

    public static final String [] FACEBOOK_PERMISSIONS = { "email", "user_birthday" };
    public static final int ERROR_RESOLUTION_REQUEST_CODE = 300;

    private LoginListener loginListener;
    private StatusBarDesigner statusBarDesigner;
    private UserSocialNetworkBuilder userSocialNetworkBuilder;

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

    private void setupObjects() {
        FacebookSdk.sdkInitialize(getActivity());

        statusBarDesigner = new StatusBarDesigner();
        userSocialNetworkBuilder = new UserSocialNetworkBuilder();
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

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
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

    @Override
    public void onSuccess(LoginResult loginResult) {
        CognitoCredentialsLoginManager.setFacebookLogin(loginResult.getAccessToken());
        requestFacebookUserInfo(loginResult);
    }

    private void requestFacebookUserInfo(LoginResult loginResult) {
        final ProgressDialog progressDialog = showLoadUserProgress();

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                progressDialog.dismiss();
                if (graphResponse != null && graphResponse.getError() == null) {
                    User user = userSocialNetworkBuilder.buildUserFromFacebook(jsonObject);
                    if (loginListener != null) loginListener.onLoginWithSocialNetwork(user);
                } else {
                    showLoginErrorAlert();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "birthday,name,email,gender,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @NonNull
    private ProgressDialog showLoadUserProgress() {
        return ProgressDialog.show(getActivity()
                    , null, getString(R.string.login_load_user_message), true, false);
    }

    @Override
    public void onCancel() {}

    @Override
    public void onError(FacebookException exception) {
        showLoginErrorAlert();
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
            CognitoCredentialsLoginManager.setGoogleLogin(googleApiClient, new OnTaskFinishedListener() {
                @Override
                public void onTaskFinished() {
                    requestUsetInfoGoogle();
                }
            });
        }

        @Override
        public void onConnectionSuspended(int connectionSuspended) {}
    };

    private void requestUsetInfoGoogle() {
        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            String email = Plus.AccountApi.getAccountName(googleApiClient);

            User user = userSocialNetworkBuilder.buildUserFromGoogle(currentPerson, email);
            loadUserDialog.dismiss();

            if(loginListener != null)
                loginListener.onLoginWithSocialNetwork(user);
        }
    }

    private GoogleApiClient.OnConnectionFailedListener googleConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            loadUserDialog.dismiss();
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
                    showLoginErrorAlert();
                }
            }
        }
    };

    private Callback<TwitterSession> twitterLoginCallback = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {
            CognitoCredentialsLoginManager.setTwitterLogin(result.data.getAuthToken());
            requestTwitterUserInfo(result.data);
        }

        @Override
        public void failure(TwitterException exception) {
            showLoginErrorAlert();
        }
    };

    private void requestTwitterUserInfo(final TwitterSession session) {
        final ProgressDialog progressDialog = showLoadUserProgress();

        TwitterApiClient apiClient = TwitterCore.getInstance().getApiClient();
        apiClient.getAccountService().verifyCredentials(false, false, new Callback<com.twitter.sdk.android.core.models.User>() {
            @Override
            public void success(Result<com.twitter.sdk.android.core.models.User> result) {
                progressDialog.dismiss();

                User user = userSocialNetworkBuilder.buildUserFromTwitter(result, session);
                if (loginListener != null) loginListener.onLoginWithSocialNetwork(user);
            }

            @Override
            public void failure(TwitterException exception) {
                progressDialog.dismiss();
                showLoginErrorAlert();
            }
        });
    }

    private View.OnClickListener onFacebookLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, LoginFragment.this);
            loginManager.logInWithReadPermissions(LoginFragment.this, Arrays.asList(FACEBOOK_PERMISSIONS));
        }
    };

    private View.OnClickListener onGoogleLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loadUserDialog = showLoadUserProgress();

            if(!googleApiClient.isConnected()) {
                googleApiClient.connect();
            } else {
                googleConnectionCallbacks.onConnected(null);
            }
        }
    };

    private View.OnClickListener onSkipLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (loginListener != null)
                loginListener.onSkipLogin();
        }
    };

    public interface LoginListener {
        void onLoginWithSocialNetwork(User user);
        void onLoginWithCredentials();
        void onSkipLogin();
        void onSignUp();
        void onUserReady(User user);
    }
}
