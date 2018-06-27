package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import in.ureport.R;
import in.ureport.fragments.ProfileFragment;
import in.ureport.listener.OnEditProfileListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class ProfileActivity extends AppCompatActivity implements OnEditProfileListener {

    private static final int REQUEST_CODE_EDIT_USER = 100;

    public static final String ACTION_DISPLAY_RANKING = "in.ureport.ProfileDisplayRanking";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null)
            addNewProfileFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_USER && resultCode == RESULT_OK)
            addNewProfileFragment();
    }

    @Override
    public void onEditProfile(User user) {
        Intent intent = new Intent(this, UserSettingsActivity.class);
        intent.putExtra(UserSettingsActivity.EXTRA_USER, user);
        startActivityForResult(intent, REQUEST_CODE_EDIT_USER);
    }

    private void addNewProfileFragment() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new ProfileFragment())
                .commit();
    }

}
