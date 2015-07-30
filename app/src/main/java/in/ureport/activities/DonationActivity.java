package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.PrototypeManager;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class DonationActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

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

        Button donateFifteen = (Button) findViewById(R.id.donateFifteen);
        donateFifteen.setOnClickListener(onDonateClickListener);

        Button donateThirty = (Button) findViewById(R.id.donateThirty);
        donateThirty.setOnClickListener(onDonateClickListener);

        Button donateSixty = (Button) findViewById(R.id.donateSixty);
        donateSixty.setOnClickListener(onDonateClickListener);
    }

    private View.OnClickListener onDonateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PrototypeManager.showPrototypeAlert(DonationActivity.this);
        }
    };

}
