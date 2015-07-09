package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

import in.ureport.R;
import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.fragments.CredentialsLoginFragment;
import in.ureport.fragments.LoginFragment;
import in.ureport.fragments.SignUpFragment;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;
import in.ureport.tasks.GetUserLoggedTask;

/**
 * Created by ilhasoft on 7/7/15.
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

        if(!userLoggedId.equals(SystemPreferences.USER_NO_LOGGED_ID)) {
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
    public void signUp() {
        addSignUpFragment();
    }

    @Override
    public void userReady(User user) {
        startMainActivity();
    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void addSignUpFragment() {
        SignUpFragment signUpFragment = new SignUpFragment();
        signUpFragment.setLoginListener(this);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content, signUpFragment)
                .addToBackStack(null)
                .commit();
    }
}
