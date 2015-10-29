package in.ureport.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.com.ilhasoft.support.tool.ScreenUtils;
import in.ureport.R;
import in.ureport.fragments.MediaFragment;
import in.ureport.models.Media;

/**
 * Created by johncordeiro on 28/09/15.
 */
public class MediaActivity extends AppCompatActivity {

    public static final String EXTRA_MEDIA = "media";

    private static final int DELAY_FULL_SCREEN = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        setupToolbar();

        if(savedInstanceState == null) {
            Media media = getIntent().getParcelableExtra(EXTRA_MEDIA);
            Fragment fragment = MediaFragment.newInstance(media);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScreenUtils screenUtils = new ScreenUtils(MediaActivity.this);
                screenUtils.hideSystemUI();
            }
        }, DELAY_FULL_SCREEN);
    }
}
