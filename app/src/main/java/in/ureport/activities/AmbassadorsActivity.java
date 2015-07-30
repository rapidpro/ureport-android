package in.ureport.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import in.ureport.R;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class AmbassadorsActivity extends AppCompatActivity {

    private static final String URL_UNICEF_AMBASSADORS = "http://www.unicefusa.org/supporters/celebrities/ambassadors";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassadors);

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

        Button seeAll = (Button) findViewById(R.id.seeAll);
        seeAll.setOnClickListener(onSeeAllClickListener);
    }

    private View.OnClickListener onSeeAllClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_UNICEF_AMBASSADORS));
            startActivity(browserIntent);
        }
    };
}
