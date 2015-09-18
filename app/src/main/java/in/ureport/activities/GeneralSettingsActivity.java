package in.ureport.activities;

import android.os.Bundle;

import in.ureport.R;
import in.ureport.fragments.GeneralSettingsFragment;

/**
 * Created by johncordeiro on 18/09/15.
 */
public class GeneralSettingsActivity extends SettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            GeneralSettingsFragment generalSettingsFragment = new GeneralSettingsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, generalSettingsFragment)
                    .commit();
        }
    }
}
