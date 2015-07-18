package in.ureport.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import in.ureport.R;
import in.ureport.fragments.NewsFragment;
import in.ureport.fragments.PollsFragment;
import in.ureport.fragments.StoriesListFragment;
import in.ureport.models.User;
import in.ureport.models.holders.NavigationItem;
import in.ureport.views.adapters.NavigationAdapter;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class MainActivity extends BaseActivity {

    private static final int POSITION_MAIN_ACTION_BUTTON = 0;
    private static final int REQUEST_CODE_CREATE_STORY = 10;

    private static final String TAG = "MainActivity";

    private ViewPager pager;

    private StoriesListFragment storiesListFragment;
    private NewsFragment newsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
    }

    @Override
    public boolean hasMainActionButton() {
        return true;
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

        getTabLayout().setupWithViewPager(pager);
        getMainActionButton().setOnClickListener(onCreateStoryClickListener);
    }

    private boolean containsMainActionButton(int position) {
        return position == POSITION_MAIN_ACTION_BUTTON;
    }

    private void setupNavigationAdapter() {
        storiesListFragment = new StoriesListFragment();
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
        newsFragment.setUser(user);
        getToolbar().setTitle(user.getCountry());
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageScrollStateChanged(int state) {}
        @Override
        public void onPageSelected(int position) {
            getMainActionButton().setVisibility(containsMainActionButton(position) ? View.VISIBLE : View.GONE);
        }
    };

    private View.OnClickListener onCreateStoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent createStoryIntent = new Intent(MainActivity.this, CreateStoryActivity.class);
            startActivityForResult(createStoryIntent, REQUEST_CODE_CREATE_STORY);
        }
    };
}
