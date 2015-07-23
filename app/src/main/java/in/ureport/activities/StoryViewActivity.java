package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.StoryViewFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class StoryViewActivity extends AppCompatActivity {

    public static final String EXTRA_STORY = "story";
    public static final String EXTRA_USER = "user";

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

                StoryViewFragment storyViewFragment = StoryViewFragment.newInstance(story, user);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content, storyViewFragment)
                        .commit();
            } else {
                finish();
            }
        }
    }
}
