package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.CredentialsLoginFragment;
import in.ureport.fragments.LoginFragment;
import in.ureport.fragments.SignUpFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;
import in.ureport.tasks.CacheLoggedUserTask;
import in.ureport.tasks.GetDbUserTask;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if(fragment != null) fragment.onActivityResult(requestCode, resultCode, data);
    }

    private void checkUserLoggedAndProceed() {
        SystemPreferences systemPreferences = new SystemPreferences(this);
        String userLoggedId = systemPreferences.getUserLoggedId();
        UserManager.userLoggedIn = !userLoggedId.equals(SystemPreferences.USER_NO_LOGGED_ID);
        UserManager.countryCode = systemPreferences.getCountryCode();

        if(UserManager.userLoggedIn) {
            CountryProgramManager.switchCountryProgram(UserManager.countryCode);
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
    public void onLoginWithCredentials() {
        addCredentialsLoginFragment();
    }

    @Override
    public void onSkipLogin() {
        createFakeDataAndProcced();
    }

    private void addCredentialsLoginFragment() {
        CredentialsLoginFragment credentialsLoginFragment = new CredentialsLoginFragment();
        credentialsLoginFragment.setLoginListener(this);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content, credentialsLoginFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLoginWithSocialNetwork(final User user) {
        new GetDbUserTask(this, R.string.user_confirmation_load_message) {
            @Override
            protected void onPostExecute(User dbUser) {
                super.onPostExecute(dbUser);
                if(dbUser != null)
                    onUserReady(dbUser);
                else
                    addSignUpFragment(user);
            }
        }.execute();
    }

    @Override
    public void onSignUp() {
        addSignUpFragment();
    }

    @Override
    public void onUserReady(final User user) {
        CacheLoggedUserTask cacheLoggedUserTask = new CacheLoggedUserTask(this) {
            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                UserManager.userLoggedIn = true;
                UserManager.countryCode = user.getCountry();

                CountryProgramManager.switchCountryProgram(user.getCountry());
                createFakeDataAndProcced();
            }
        };
        cacheLoggedUserTask.execute(user);
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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content, signUpFragment)
                .addToBackStack(null)
                .commit();
    }
}
