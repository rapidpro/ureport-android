package in.ureport.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.fragments.CreateStoryFragment;
import in.ureport.fragments.InviteCoauthorFragment;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateStoryActivity extends AppCompatActivity implements CreateStoryFragment.StoryCreationListener, InviteCoauthorFragment.InviteCoauthorResultListener {

    private CreateStoryFragment createStoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);
        setupView();

        if(savedInstanceState == null) {
            addCreateStoryFragment();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            finish();

        return true;
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addCreateStoryFragment() {
        createStoryFragment = new CreateStoryFragment();
        createStoryFragment.setStoryCreationListener(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, createStoryFragment)
                .commit();
    }

    @Override
    public void onCoauthorsInviteResult(List<User> selectedCoauthors) {
        createStoryFragment.setSelectedCoauthors(selectedCoauthors);
    }

    @Override
    public void addCoauthors(List<User> selectedCoauthors) {
        InviteCoauthorFragment inviteCoauthorFragment;
        if(selectedCoauthors != null && selectedCoauthors.size() > 0)
            inviteCoauthorFragment = InviteCoauthorFragment.newInstance((ArrayList<User>)selectedCoauthors);
        else
            inviteCoauthorFragment = new InviteCoauthorFragment();

        inviteCoauthorFragment.setInviteCoauthorResultListener(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, inviteCoauthorFragment)
                .addToBackStack(null)
                .commit();
    }
}
