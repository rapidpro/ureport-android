package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.StatisticsFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class StatisticsActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_generic);

        Bundle extras = getIntent().getExtras();
        if(savedInstanceState == null) {
            if(extras != null && extras.containsKey(EXTRA_USER)) {
                User user = extras.getParcelable(EXTRA_USER);

                StatisticsFragment statisticsFragment = StatisticsFragment.newInstance(user);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content, statisticsFragment)
                        .commit();
            } else {
                finish();
            }
        }
    }

}
