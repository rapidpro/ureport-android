package in.ureport.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import in.ureport.R;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.DonationManager;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class DonationActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "user";

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_donation);

        user = getIntent().getParcelableExtra(EXTRA_USER);
        setupView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button donate = (Button) findViewById(R.id.donate);
        donate.setOnClickListener(onDonateClickListener);
    }

    private View.OnClickListener onDonateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(user != null && DonationManager.getDonationUrl(user) != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(DonationManager.getDonationUrl(user)));
                startActivity(browserIntent);
            }
        }
    };

}
