package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.StoryViewFragment;
import in.ureport.models.Story;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class StoryViewActivity extends AppCompatActivity {

    public static final String EXTRA_STORY = "story";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);

        Bundle extras = getIntent().getExtras();

        if(savedInstanceState == null) {
            if(extras != null && extras.containsKey(EXTRA_STORY)) {
                Story story = extras.getParcelable(EXTRA_STORY);

                StoryViewFragment storyViewFragment = StoryViewFragment.newInstance(story);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content, storyViewFragment)
                        .commit();
            } else {
                finish();
            }
        }
    }
}
