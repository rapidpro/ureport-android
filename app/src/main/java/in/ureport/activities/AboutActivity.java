package in.ureport.activities;

import android.os.Bundle;
import android.widget.TextView;

import in.ureport.R;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class AboutActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupView();
    }

    private void setupView() {
        getMenuNavigation().getMenu().findItem(R.id.about).setChecked(true);
        addHeaderToAppbar();
    }

    private void addHeaderToAppbar() {
        TextView headerInstitutional = (TextView) getLayoutInflater().inflate(R.layout.view_header_institutional, null);
        headerInstitutional.setText(R.string.about_subtitle);
        getAppBar().addView(headerInstitutional);
    }

    @Override
    public boolean hasTabLayout() {
        return false;
    }
}
