package in.ureport.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.fragments.MediaViewFragment;
import in.ureport.fragments.NewsViewFragment;
import in.ureport.fragments.RecordAudioFragment;
import in.ureport.fragments.StoryViewFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.Media;
import in.ureport.models.News;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class StoryViewActivity extends AppCompatActivity implements MediaAdapter.OnMediaViewListener
        , MediaViewFragment.OnCloseMediaViewListener {

    public static final String ACTION_LOAD_STORY = "in.ureport.LoadStory";

    private static final String DOCUMENT_URL = "http://docs.google.com/gview?embedded=true&url=%1$s";

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
    public void onMediaView(List<Media> medias, int position) {
        MediaViewFragment mediaViewFragment = MediaViewFragment.newInstance((ArrayList<Media>)medias, position);
        addFragment(mediaViewFragment);
    }

    @Override
    public void onVideoMediaView(Media media) {
        MediaViewFragment mediaViewFragment = MediaViewFragment.newInstance(media);
        addFragment(mediaViewFragment);
    }

    @Override
    public void onFileMediaView(Media media) {
        Intent viewFileIntent = new Intent(Intent.ACTION_VIEW);
        String url = media.getUrl();
        if(isGoogleDocsSupported(url))
            url = String.format(DOCUMENT_URL, media.getUrl());
        viewFileIntent.setData(Uri.parse(url));
        startActivity(viewFileIntent);
    }

    private boolean isGoogleDocsSupported(String url) {
        return url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx")
        || url.endsWith(".ppt") || url.endsWith(".xls") || url.endsWith(".xlsx")
        || url.endsWith(".csv") || url.endsWith(".ods") || url.endsWith(".txt")
        || url.endsWith(".svg");
    }

    @Override
    public void onAudioMediaView(Media media) {
        RecordAudioFragment recordAudioFragment = RecordAudioFragment.newInstance(media);
        recordAudioFragment.show(getSupportFragmentManager(), "recordAudioFragment");
    }

    @Override
    public void onCloseMediaView() {
        getSupportFragmentManager().popBackStack();
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }
}
