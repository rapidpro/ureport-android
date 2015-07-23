package in.ureport.activities;

import android.os.Bundle;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.managers.CountryProgramManager;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class DonationActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_donation);

        setupView();
    }

    private void setupView() {
        getMenuNavigation().getMenu().findItem(R.id.makeDonations).setChecked(true);
        addHeaderToAppbar();
    }

    private void addHeaderToAppbar() {
        TextView headerInstitutional = (TextView) getLayoutInflater().inflate(R.layout.view_header_institutional, null);
        headerInstitutional.setText(R.string.label_make_donation_subtitle);
        getAppBar().addView(headerInstitutional);
    }

    @Override
    public boolean hasTabLayout() {
        return false;
    }
}
