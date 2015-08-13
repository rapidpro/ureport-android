package in.ureport.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import in.ureport.R;
import in.ureport.UreportApplication;
import in.ureport.fragments.NewsFragment;
import in.ureport.fragments.PollsFragment;
import in.ureport.fragments.StoriesListFragment;
import in.ureport.listener.FloatingActionButtonListener;
import in.ureport.managers.CognitoCredentialsLoginManager;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.User;
import in.ureport.models.holders.NavigationItem;
import in.ureport.views.adapters.NavigationAdapter;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class MainActivity extends BaseActivity implements FloatingActionButtonListener
        , StoriesAdapter.OnPublishStoryListener {

    private static final int POSITION_MAIN_ACTION_BUTTON = 0;
    private static final int REQUEST_CODE_CREATE_STORY = 10;

    private static final String TAG = "MainActivity";
    public static final String EXTRA_FORCED_LOGIN = "forcedLogin";

    private ViewPager pager;

    private StoriesListFragment storiesListFragment;
    private NewsFragment newsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CognitoCredentialsLoginManager.refresh();

        checkForcedLogin();
        setContentView(R.layout.activity_main);
        setupView();
    }

    private void checkForcedLogin() {
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(EXTRA_FORCED_LOGIN)) {
            Boolean forcedLogin = extras.getBoolean(EXTRA_FORCED_LOGIN, false);

            if(forcedLogin) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }
    }

    @Override
    public boolean hasMainActionButton() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifications:
                openEndDrawer();
                break;
            case R.id.chat:
                startChatActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startChatActivity() {
        if(UreportApplication.validateUserLogin(MainActivity.this)) {
            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_CODE_CREATE_STORY:
                if(resultCode == Activity.RESULT_OK) {
                    storiesListFragment.loadStories();
                }
        }
    }

    private void setupView() {
        pager = (ViewPager) findViewById(R.id.pager);
        pager.addOnPageChangeListener(onPageChangeListener);
        setupNavigationAdapter();
        hideFloatingButtonDelayed();

        getTabLayout().setupWithViewPager(pager);
        getMainActionButton().setOnClickListener(onCreateStoryClickListener);
        getMenuNavigation().getMenu().findItem(R.id.home).setChecked(true);
    }

    private void hideFloatingButtonDelayed() {
        getMainActionButton().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFloatingButton();
            }
        }, 1000);
    }

    private boolean containsMainActionButton(int position) {
        return position == POSITION_MAIN_ACTION_BUTTON;
    }

    private void setupNavigationAdapter() {
        storiesListFragment = new StoriesListFragment();
        storiesListFragment.setFloatingActionButtonListener(this);
        storiesListFragment.setOnPublishStoryListener(this);
        NavigationItem storiesItem = new NavigationItem(storiesListFragment, getString(R.string.main_stories));
        NavigationItem pollsItem = new NavigationItem(new PollsFragment(), getString(R.string.main_polls));

        newsFragment = NewsFragment.newInstance(getLoggedUser());
        NavigationItem newsItem = new NavigationItem(newsFragment, getString(R.string.main_news_feed));

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager()
                , storiesItem, pollsItem, newsItem);

        pager.setAdapter(adapter);
    }

    @Override
    public void setUser(User user) {
        super.setUser(user);

        if(user != null) {
            newsFragment.setUser(user);
            storiesListFragment.updateUser(user);
            getToolbar().setTitle(CountryProgramManager.getCurrentCountryProgram().getName());
        }
    }

    @Override
    public void showFloatingButton() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getMainActionButton().animate().translationY(0).start();
        } else {
            getMainActionButton().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideFloatingButton() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getMainActionButton().animate().translationY(getMainActionButton().getHeight()
                    + getResources().getDimension(R.dimen.fab_margin)).start();
        } else {
            getMainActionButton().setVisibility(View.GONE);
        }
    }

    private void checkFloatingButtonVisibility(int position) {
        if(containsMainActionButton(position)) {
            showFloatingButton();
        } else {
            hideFloatingButton();
        }
    }

    private void publishStory() {
        if(UreportApplication.validateUserLogin(MainActivity.this)) {
            Intent createStoryIntent = new Intent(MainActivity.this, CreateStoryActivity.class);
            startActivityForResult(createStoryIntent, REQUEST_CODE_CREATE_STORY);
        }
    }

    @Override
    public void onPublishStory() {
        publishStory();
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageScrollStateChanged(int state) {}
        @Override
        public void onPageSelected(int position) {
            checkFloatingButtonVisibility(position);
        }
    };

    private View.OnClickListener onCreateStoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            publishStory();
        }
    };
}
