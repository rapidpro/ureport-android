package in.ureport.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

import in.ureport.R;
import in.ureport.fragments.CredentialsLoginFragment;
import in.ureport.fragments.ForgotPasswordFragment;
import in.ureport.fragments.LoginFragment;
import in.ureport.fragments.SignUpFragment;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.network.UserServices;
import in.ureport.tasks.SaveContactTask;
import io.rapidpro.sdk.core.models.base.ContactBase;

/**
 * Created by johncordeiro on 7/7/15.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.LoginListener {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(savedInstanceState == null) {
            addLoginFragment();
        }
        checkVersionAndProceed();
    }

    private void checkVersionAndProceed() {
        if(UserManager.hasOldVersion()) {
            UserManager.removeOldVersionFlag(this);
            UserManager.logout(this);
        } else {
            checkUserLoggedAndProceed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if(fragment != null) fragment.onActivityResult(requestCode, resultCode, data);
    }

    private void checkUserLoggedAndProceed() {
        if(UserManager.isUserLoggedIn() && UserManager.isCountryCodeValid()) {
            startMainActivity();
        }
    }

    private void addLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, loginFragment)
                .commit();
    }

    @Override
    public void onLoginWithCredentials() {
        addCredentialsLoginFragment();
    }

    @Override
    public void onSkipLogin() {
        startMainActivity();
    }

    private void addCredentialsLoginFragment() {
        CredentialsLoginFragment credentialsLoginFragment = new CredentialsLoginFragment();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content, credentialsLoginFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLoginWithSocialNetwork(final User user) {
        UserServices userServices = new UserServices();
        userServices.getUser(user.getKey(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setKey(dataSnapshot.getKey());
                    onUserReady(user, false);
                } else {
                    addSignUpFragment(user);
                }
            }
        });
    }

    @Override
    public void onSignUp() {
        addSignUpFragment();
    }

    @Override
    public void onUserReady(final User user, boolean newUser) {
        if(user == null) return;
        saveContactOnRapidPro(user, newUser);
    }

    private void saveContactOnRapidPro(final User user, final boolean newUser) {
        SaveContactTask saveContactTask = new SaveContactTask(this, newUser) {
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(LoginActivity.this, null
                        , getString(R.string.load_message_wait), true, false);
            }
            @Override
            protected void onPostExecute(ContactBase contact) {
                super.onPostExecute(contact);
                if (progressDialog != null) progressDialog.dismiss();
                updateUserAndDismiss(user);
            }
        };
        saveContactTask.execute(user);
    }

    private void updateUserAndDismiss(User user) {
        UserManager.updateUserInfo(user, this::startMainActivity);
    }

    @Override
    public void onForgotPassword() {
        ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, forgotPasswordFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPasswordReset() {
        getSupportFragmentManager().popBackStack();
        Toast.makeText(this, R.string.error_email_check, Toast.LENGTH_LONG).show();
    }

    private void startMainActivity() {
        Intent mainIntent = MainActivity.createIntent(this);
        if (getIntent().getExtras() != null) {
            mainIntent.putExtras(getIntent().getExtras());
        }
        startActivity(mainIntent);
        finish();
    }

    private void addSignUpFragment() {
        addSignUpFragment(null);
    }

    private void addSignUpFragment(User user) {
        SignUpFragment signUpFragment = SignUpFragment.newInstance(user);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content, signUpFragment)
                .addToBackStack(null)
                .commit();
    }
}
