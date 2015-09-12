package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import in.ureport.R;
import in.ureport.fragments.PollsFragment;

/**
 * Created by johncordeiro on 12/09/15.
 */
public class LastPollsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_polls);

        setupToolbar();

        if(savedInstanceState == null) {
            PollsFragment pollsFragment = new PollsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, pollsFragment)
                    .commit();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
