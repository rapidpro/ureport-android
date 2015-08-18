package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.ProfileFragment;
import in.ureport.managers.CountryProgramManager;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class ProfileActivity extends AppCompatActivity {

    private ProfileFragment profileFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_profile);

        if(savedInstanceState == null) {
            profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, profileFragment)
                    .commit();
        }
    }

}
