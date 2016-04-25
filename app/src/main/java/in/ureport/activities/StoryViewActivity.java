package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.MediaViewFragment;
import in.ureport.fragments.NewsViewFragment;
import in.ureport.fragments.StoryViewFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.News;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class StoryViewActivity extends AppCompatActivity implements MediaViewFragment.OnCloseMediaViewListener {

    public static final String ACTION_LOAD_STORY = "in.ureport.LoadStory";

    public static final String EXTRA_STORY = "story";
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_NEWS = "news";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_generic);

        Bundle extras = getIntent().getExtras();

        if(savedInstanceState == null) {
            if(extras != null && extras.containsKey(EXTRA_STORY)) {
                Story story = extras.getParcelable(EXTRA_STORY);
                User user = extras.getParcelable(EXTRA_USER);
                addStoryViewFragment(story, user);
            } else if(extras != null && extras.containsKey(EXTRA_NEWS)) {
                News news = extras.getParcelable(EXTRA_NEWS);
                addNewsViewFragment(news);
            } else {
                supportFinishAfterTransition();
            }
        }
    }

    private void addNewsViewFragment(News news) {
        NewsViewFragment newsViewFragment = NewsViewFragment.newInstance(news);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, newsViewFragment)
                .commit();
    }

    private void addStoryViewFragment(Story story, User user) {
        boolean isStoryLoaded = getIntent().getAction() == null || !getIntent().getAction().equals(ACTION_LOAD_STORY);

        StoryViewFragment storyViewFragment = StoryViewFragment.newInstance(story, user, isStoryLoaded);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, storyViewFragment)
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return false;
        } else {
            supportFinishAfterTransition();
        }
        return true;
    }

    @Override
    public void onCloseMediaView() {
        getSupportFragmentManager().popBackStack();
    }

}
