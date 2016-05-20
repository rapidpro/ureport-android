package in.ureport.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import br.com.ilhasoft.support.tool.EditTextValidator;
import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.managers.FirebaseManager;
import in.ureport.helpers.ToolbarDesigner;
import in.ureport.models.User;
import in.ureport.models.holders.Login;
import in.ureport.network.UserServices;
import in.ureport.helpers.ValueEventListenerAdapter;

/**
 * Created by johncordeiro on 7/7/15.
 */
public class CredentialsLoginFragment extends Fragment {

    private EditText email;
    private EditText password;

    private EditTextValidator validator = new EditTextValidator();

    private LoginFragment.LoginListener loginListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credentials_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        setLoginStatusBarColor();
    }

    private void setLoginStatusBarColor() {
        StatusBarDesigner statusBarDesigner = new StatusBarDesigner();
        statusBarDesigner.setStatusBarColorById(getActivity(), R.color.dark_green_highlight);
    }

    private void setupView(View view) {
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);

        TextView forgotPassword = (TextView) view.findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(onForgotPasswordClickListener);

        Button login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(onLoginClickListener);

        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        ToolbarDesigner toolbarDesigner = new ToolbarDesigner();
        toolbarDesigner.setupFragmentDefaultToolbar(toolbar, R.string.label_login, this);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if(activity instanceof LoginFragment.LoginListener) {
            loginListener = (LoginFragment.LoginListener)activity;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateFields() {
        boolean validEmail = validator.validateEmpty(email, getString(R.string.error_required_field));
        boolean validPassword = validator.validateEmpty(password, getString(R.string.error_required_field));

        return validEmail && validPassword;
    }

    private void login(Login login) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.load_message_logging), true, false);

        FirebaseManager.getReference().authWithPassword(login.getEmail(), login.getPassword(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                getUserInfoAndContinue(authData, progressDialog);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                progressDialog.dismiss();
                showLoginError();
            }
        });
    }

    private void getUserInfoAndContinue(AuthData authData, final ProgressDialog progressDialog) {
        UserServices userServices = new UserServices();
        userServices.getUser(authData.getUid(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                progressDialog.dismiss();

                User user = dataSnapshot.getValue(User.class);
                loginListener.onUserReady(user, false);
            }
        });
    }

    private void showLoginError() {
        Toast.makeText(getActivity(), "Email/password not found", Toast.LENGTH_LONG).show();
    }

    private View.OnClickListener onLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (validateFields()) {
                Login login = new Login(email.getText().toString()
                        , password.getText().toString());
                login(login);
            }
        }
    };

    private View.OnClickListener onForgotPasswordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(loginListener != null)
                loginListener.onForgotPassword();
        }
    };
}
