package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.UreportApplication;
import in.ureport.fragments.CredentialsLoginFragment;
import in.ureport.fragments.LoginFragment;
import in.ureport.fragments.SignUpFragment;
import in.ureport.managers.UserLoginManager;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;
import in.ureport.tasks.CreateFakeDataTask;

/**
 * Created by johncordeiro on 7/7/15.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.LoginListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(savedInstanceState == null) {
            addLoginFragment();
        }
        checkUserLoggedAndProceed();
    }

    private void checkUserLoggedAndProceed() {
        SystemPreferences systemPreferences = new SystemPreferences(this);
        Long userLoggedId = systemPreferences.getUserLoggedId();
        UserLoginManager.userLoggedIn = !userLoggedId.equals(SystemPreferences.USER_NO_LOGGED_ID);

        if(UserLoginManager.userLoggedIn) {
            startMainActivity();
        }
    }

    private void addLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setLoginListener(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, loginFragment)
                .commit();
    }

    @Override
    public void loginWithCredentials() {
        addCredentialsLoginFragment();
    }

    @Override
    public void skipLogin() {
        createFakeDataAndProcced();
    }

    private void addCredentialsLoginFragment() {
        CredentialsLoginFragment credentialsLoginFragment = new CredentialsLoginFragment();
        credentialsLoginFragment.setLoginListener(this);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content, credentialsLoginFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void loginWithSocialNetwork(User user) {
        addSignUpFragment(user);
    }

    @Override
    public void signUp() {
        addSignUpFragment();
    }

    @Override
    public void userReady(User user) {
        UserLoginManager.userLoggedIn = true;
        createFakeDataAndProcced();
    }

    private void createFakeDataAndProcced() {
        new CreateFakeDataTask(this) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                startMainActivity();
            }
        }.execute();
    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    private void addSignUpFragment() {
        addSignUpFragment(null);
    }

    private void addSignUpFragment(User user) {
        SignUpFragment signUpFragment = SignUpFragment.newInstance(user);
        signUpFragment.setLoginListener(this);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content, signUpFragment)
                .addToBackStack(null)
                .commit();
    }
}
