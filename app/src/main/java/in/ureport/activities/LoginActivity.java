package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import in.ureport.R;
import in.ureport.fragments.CredentialsLoginFragment;
import in.ureport.fragments.ForgotPasswordFragment;
import in.ureport.fragments.LoginFragment;
import in.ureport.fragments.SignUpFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.network.UserServices;

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
        AuthData authData = FirebaseManager.getReference().getAuth();
        UserManager.userLoggedIn = authData != null;

        if(UserManager.userLoggedIn) {
            loadUserAndContinue(authData);
        }
    }

    private void loadUserAndContinue(AuthData authData) {
        UserServices userServices = new UserServices();
        userServices.getUser(authData.getUid(), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    UserManager.countryCode = user.getCountry();
                    CountryProgramManager.switchCountryProgram(UserManager.countryCode);
                    startMainActivity();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
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
        startMainActivity();
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
        UserServices userServices = new UserServices();
        userServices.getUser(user.getKey(), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("LoginActivity", "onComplete childrenCount: " + dataSnapshot.getChildrenCount());

                if(dataSnapshot.exists())
                    onUserReady(dataSnapshot.getValue(User.class));
                else
                    addSignUpFragment(user);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    @Override
    public void onSignUp() {
        addSignUpFragment();
    }

    @Override
    public void onUserReady(final User user) {
        UserServices userServices = new UserServices();
        userServices.keepUserOffline(user);

        UserManager.userLoggedIn = true;
        UserManager.countryCode = user.getCountry();

        CountryProgramManager.switchCountryProgram(user.getCountry());
        startMainActivity();
    }

    @Override
    public void onForgotPassword() {
        ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
        forgotPasswordFragment.setLoginListener(this);
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
