package in.ureport.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import in.ureport.R;
import in.ureport.fragments.ChangePasswordFragment;
import in.ureport.fragments.EditUserFragment;
import in.ureport.fragments.UserSettingsFragment;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 11/09/15.
 */
public class UserSettingsActivity extends SettingsActivity implements UserSettingsFragment.UserSettingsListener {

    public static final String EXTRA_USER = "user";

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            user = getIntent().getParcelableExtra(EXTRA_USER);

            UserSettingsFragment userSettingsFragment = UserSettingsFragment.newInstance(user);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, userSettingsFragment)
                    .commit();
        }
    }

    @Override
    public void onEditProfile() {
        EditUserFragment editUserFragment = EditUserFragment.newInstance(user);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content, editUserFragment)
                .commit();
    }

    @Override
    public void onChangePassword() {
        ChangePasswordFragment changePasswordFragment = ChangePasswordFragment.newInstance(user);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content, changePasswordFragment)
                .commit();
    }

    @Override
    public void onEditFinished() {
        setResult(RESULT_OK);
        finish();
    }
}
