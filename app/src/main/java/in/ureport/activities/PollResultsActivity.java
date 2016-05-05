package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import in.ureport.R;
import in.ureport.fragments.PollAllResultsFragment;
import in.ureport.fragments.PollRegionResultsFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.Poll;
import in.ureport.models.PollResult;
import in.ureport.views.adapters.PollResultsAdapter;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class PollResultsActivity extends AppCompatActivity implements PollResultsAdapter.PollResultsListener {

    public static final String EXTRA_POLL = "poll";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_poll_results);

        setupView();

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null && extras.containsKey(EXTRA_POLL)) {
                Poll poll = extras.getParcelable(EXTRA_POLL);

                PollAllResultsFragment pollAllResultsFragment = PollAllResultsFragment.newInstance(poll);
                pollAllResultsFragment.setPollResultsListener(this);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content, pollAllResultsFragment)
                        .commit();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
        return true;
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.label_poll_results);
    }

    @Override
    public void onViewResultByRegion(PollResult pollResult) {
        PollRegionResultsFragment pollRegionResultsFragment = PollRegionResultsFragment.newInstance(pollResult);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, pollRegionResultsFragment)
                .addToBackStack(null)
                .commit();
    }
}
