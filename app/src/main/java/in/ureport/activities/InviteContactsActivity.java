package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import in.ureport.R;
import in.ureport.fragments.InviteContactsFragment;
import in.ureport.managers.CountryProgramManager;

/**
 * Created by johncordeiro on 11/09/15.
 */
public class InviteContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_invite_contacts);
        setupToolbar();

        if(savedInstanceState == null) {
            InviteContactsFragment inviteContactsFragment = new InviteContactsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, inviteContactsFragment)
                    .commit();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
