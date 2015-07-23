package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.models.User;
import in.ureport.tasks.SocialNetworkLoginTask;

/**
 * Created by johncordeiro on 7/7/15.
 */
public class LoginFragment extends Fragment {

    private LoginListener loginListener;
    private StatusBarDesigner statusBarDesigner;

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

    private void setupObjects() {
        statusBarDesigner = new StatusBarDesigner();
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
        loginWithTwitter.setOnClickListener(new OnSocialNetworkClickListener(User.Type.Twitter));

        Button loginWithGoogle = (Button) view.findViewById(R.id.loginWithGoogle);
        loginWithGoogle.setOnClickListener(new OnSocialNetworkClickListener(User.Type.Google));

        Button loginWithFacebook = (Button) view.findViewById(R.id.loginWithFacebook);
        loginWithFacebook.setOnClickListener(new OnSocialNetworkClickListener(User.Type.Facebook));

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
                loginListener.loginWithCredentials();
            }
        }
    };

    private View.OnClickListener onSignUpClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (loginListener != null) {
                loginListener.signUp();
            }
        }
    };

    private class OnSocialNetworkClickListener implements View.OnClickListener {

        private User.Type type;

        public OnSocialNetworkClickListener(User.Type type) {
            this.type = type;
        }

        @Override
        public void onClick(View view) {
            SocialNetworkLoginTask socialNetworkLoginTask = new SocialNetworkLoginTask(getActivity()){
                @Override
                protected void onPostExecute(User user) {
                    super.onPostExecute(user);
                    if(loginListener != null) {
                        loginListener.loginWithSocialNetwork(user);
                    }
                }
            };
            socialNetworkLoginTask.execute(type);
        }
    }

    private View.OnClickListener onSkipLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (loginListener != null)
                loginListener.skipLogin();
        }
    };

    public static interface LoginListener {
        void loginWithSocialNetwork(User user);
        void loginWithCredentials();
        void skipLogin();
        void signUp();
        void userReady(User user);
    }
}
