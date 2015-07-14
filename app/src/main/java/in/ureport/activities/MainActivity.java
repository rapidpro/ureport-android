package in.ureport.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import in.ureport.R;
import in.ureport.fragments.NewsFragment;
import in.ureport.fragments.PollsFragment;
import in.ureport.fragments.StoriesListFragment;
import in.ureport.models.User;
import in.ureport.models.holders.NavigationItem;
import in.ureport.tasks.GetUserLoggedTask;
import in.ureport.views.adapters.MainNavigationAdapter;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class MainActivity extends BaseActivity {

    private ViewPager pager;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
        loadUser();
    }

    private void setupView() {
        pager = (ViewPager) findViewById(R.id.pager);
        setupNavigationAdapter();

        getTabLayout().setupWithViewPager(pager);
    }

    private void loadUser() {
        loadUserTask.execute();
    }

    private void setupNavigationAdapter() {
        NavigationItem storiesItem = new NavigationItem(new StoriesListFragment(), getString(R.string.main_stories));
        NavigationItem pollsItem = new NavigationItem(new PollsFragment(), getString(R.string.main_polls));
        NavigationItem newsItem = new NavigationItem(new NewsFragment(), getString(R.string.main_news_feed));

        MainNavigationAdapter adapter = new MainNavigationAdapter(getSupportFragmentManager()
                , storiesItem, pollsItem, newsItem);

        pager.setAdapter(adapter);
    }

    private GetUserLoggedTask loadUserTask = new GetUserLoggedTask(this) {
        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            MainActivity.this.user = user;
            getToolbar().setTitle(user.getCountry());
        }
    };
}
