package in.ureport.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.CredentialsLoginFragment;
import in.ureport.fragments.LoginFragment;

/**
 * Created by ilhasoft on 7/7/15.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.LoginListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addLoginFragment();
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
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content, credentialsLoginFragment)
                .addToBackStack(null)
                .commit();
    }
}
