package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.models.User;

/**
 * Created by ilhasoft on 7/7/15.
 */
public class LoginFragment extends Fragment {

    private LoginListener loginListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        Button loginWithCredentials = (Button) view.findViewById(R.id.loginWithCredentials);
        loginWithCredentials.setOnClickListener(onLoginWithCredentialsClickListener);

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

    public static interface LoginListener {
        void loginWithCredentials();
        void signUp();
        void userReady(User user);
    }
}
