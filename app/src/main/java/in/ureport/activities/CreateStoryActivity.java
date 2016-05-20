package in.ureport.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.utils.KeyboardHandler;
import in.ureport.R;
import in.ureport.fragments.CreateStoryFragment;
import in.ureport.fragments.MarkersFragment;
import in.ureport.listener.SelectionResultListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.GameficationManager;
import in.ureport.managers.UserViewManager;
import in.ureport.models.Marker;
import in.ureport.models.Story;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateStoryActivity extends AppCompatActivity implements CreateStoryFragment.StoryCreationListener, SelectionResultListener<Marker> {

    public static final String EXTRA_USER = "user";

    private static final String TAG_CREATE_STORY = "createStoryFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_create_story);
        setupView();

        if(savedInstanceState == null) {
            addCreateStoryFragment();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addCreateStoryFragment() {
        CreateStoryFragment createStoryFragment = new CreateStoryFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, createStoryFragment, TAG_CREATE_STORY)
                .commit();
    }

    @Override
    public void onAddMarkers(List<Marker> selectedMarkers) {
        KeyboardHandler keyboardHandler = new KeyboardHandler();
        keyboardHandler.changeKeyboardVisibility(this, false);

        MarkersFragment markersFragment;
        if(selectedMarkers != null && selectedMarkers.size() > 0)
            markersFragment = MarkersFragment.newInstance((ArrayList<Marker>)selectedMarkers);
        else
            markersFragment = new MarkersFragment();

        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, markersFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStoryCreated(final Story story) {
        UserViewManager userViewManager = new UserViewManager(this);
        userViewManager.showStoryPublishingWarning(() -> showPointsEarningAndClose(story));
    }

    private void showPointsEarningAndClose(final Story story) {
        GameficationManager gameficationManager = new GameficationManager(this);
        gameficationManager.showGameficationAlert(story, () -> {
            setResult(Activity.RESULT_OK);
            finish();
        });
    }

    @Override
    public void onSelectionResult(List<Marker> markers) {
        CreateStoryFragment createStoryFragment = (CreateStoryFragment) getSupportFragmentManager().findFragmentByTag(TAG_CREATE_STORY);
        if(createStoryFragment != null)
            createStoryFragment.setSelectedMarkers(markers);
    }

}
