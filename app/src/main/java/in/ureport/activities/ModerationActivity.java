package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import in.ureport.R;
import in.ureport.fragments.StoriesModerationFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.holders.NavigationItem;
import in.ureport.views.adapters.NavigationAdapter;

/**
 * Created by johncordeiro on 16/09/15.
 */
public class ModerationActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderation);

        boolean hasPermission = checkModerationPermission();
        if(hasPermission) {
            setupView();
        }
    }

    private Boolean checkModerationPermission() {
        if(!UserManager.canModerate()) {
            exitFromModeration();
            return false;
        }
        return true;
    }

    private void exitFromModeration() {
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

    private void setupView() {
        String countryProgramName = CountryProgramManager.getCurrentCountryProgram().getName();
        setTitle(getString(R.string.title_moderation, countryProgramName));

        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        NavigationItem storiesModeration = new NavigationItem(new StoriesModerationFragment()
                , getString(R.string.stories_moderation));

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(), storiesModeration);
        pager.setAdapter(adapter);
        getTabLayout().setupWithViewPager(pager);

        getMenuNavigation().getMenu().findItem(R.id.moderation).setChecked(true);
    }
}
