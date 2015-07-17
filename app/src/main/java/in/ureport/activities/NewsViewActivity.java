package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.NewsViewFragment;
import in.ureport.fragments.StoryViewFragment;
import in.ureport.models.News;
import in.ureport.models.Story;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class NewsViewActivity extends AppCompatActivity {

    public static final String EXTRA_NEWS = "news";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null && extras.containsKey(EXTRA_NEWS)) {
                News news = extras.getParcelable(EXTRA_NEWS);

                NewsViewFragment newsViewFragment = NewsViewFragment.newInstance(news);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content, newsViewFragment)
                        .commit();
            } else {
                finish();
            }
        }
    }
}
