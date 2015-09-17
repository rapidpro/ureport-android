package in.ureport.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.fragments.MediaViewFragment;
import in.ureport.fragments.StoryViewFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.Media;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class StoryViewActivity extends AppCompatActivity implements MediaAdapter.OnMediaViewListener, MediaViewFragment.OnCloseMediaViewListener {

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

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return false;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public void onMediaView(List<Media> medias, int position) {
        MediaViewFragment mediaViewFragment = MediaViewFragment.newInstance((ArrayList<Media>)medias, position);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, mediaViewFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCloseMediaView() {
        getSupportFragmentManager().popBackStack();
    }
}
