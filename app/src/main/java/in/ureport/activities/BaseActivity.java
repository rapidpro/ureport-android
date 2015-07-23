package in.ureport.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import in.ureport.R;
import in.ureport.loader.NotificationLoader;
import in.ureport.managers.SpinnerColorSwitcher;
import in.ureport.managers.UserDataManager;
import in.ureport.managers.UserLoginManager;
import in.ureport.models.Notification;
import in.ureport.models.User;
import in.ureport.tasks.GetUserLoggedTask;
import in.ureport.util.DividerItemDecoration;
import in.ureport.views.adapters.NotificationAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public abstract class BaseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Notification>> {

    private static final String URL_UNICEF_SHOP = "http://www.unicefusa.org/help/shop";
    private static final String URL_UNICEF_AMBASSADORS = "http://www.unicefusa.org/supporters/celebrities/ambassadors";

    private AppBarLayout appBar;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private NavigationView menuNavigation;
    private FloatingActionButton mainActionButton;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private RecyclerView notificationsList;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        setupBaseView();
        loadData();
    }

    private void setupBaseView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appBar = (AppBarLayout) findViewById(R.id.appbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setVisibility(hasTabLayout() ? View.VISIBLE : View.GONE);

        mainActionButton = (FloatingActionButton) findViewById(R.id.mainActionButton);
        mainActionButton.setVisibility(hasMainActionButton() ? View.VISIBLE : View.GONE);

        menuNavigation = (NavigationView) findViewById(R.id.menuNavigation);
        menuNavigation.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout
                , toolbar, R.string.base_menu_open_drawer, R.string.base_menu_close_drawer);

        notificationsList = (RecyclerView) findViewById(R.id.notificationsList);
        notificationsList.setLayoutManager(new LinearLayoutManager(this));
        notificationsList.addItemDecoration(new DividerItemDecoration(this));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        actionBarDrawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup content = (ViewGroup) findViewById(R.id.content);

        View view = getLayoutInflater().inflate(layoutResID, null);
        content.addView(view);
    }

    private void loadData() {
        loadUserTask.execute();
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private GetUserLoggedTask loadUserTask = new GetUserLoggedTask(this) {
        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            updateUserInfo(user);
            user = refreshUserCountry(user);
            setUser(user);
        }
    };

    @NonNull
    private User refreshUserCountry(User user) {
        if(user == null) {
            user = new User();
            user.setCountry(Locale.getDefault().getDisplayCountry());
        }
        return user;
    }

    private void updateUserInfo(User user) {
        BaseActivity.this.user = user;
        if(user != null) {
            View menuHeader = getLayoutInflater().inflate(R.layout.view_header_menu, null);
            menuHeader.setOnClickListener(onMenuHeaderClickListener);

            ImageView picture = (ImageView) menuHeader.findViewById(R.id.picture);
            picture.setImageResource(UserDataManager.getUserImage(this, user));

            TextView name = (TextView) menuHeader.findViewById(R.id.name);
            name.setText("@" + user.getUsername());

            TextView points = (TextView) menuHeader.findViewById(R.id.points);
            points.setText(getString(R.string.menu_points, user.getPoints()));

            TextView polls = (TextView) menuHeader.findViewById(R.id.polls);
            polls.setText(getString(R.string.profile_polls, user.getPolls()));

            TextView stories = (TextView) menuHeader.findViewById(R.id.stories);
            stories.setText(getString(R.string.profile_stories, user.getStories()));

            Spinner countryPrograms = (Spinner) menuHeader.findViewById(R.id.countryPrograms);
            countryPrograms.setAdapter(getCountryProgramsAdapter());

            SpinnerColorSwitcher spinnerColorSwitcher = new SpinnerColorSwitcher(this);
            spinnerColorSwitcher.switchToColor(countryPrograms, android.R.color.white);

            menuNavigation.addHeaderView(menuHeader);
        }
    }

    @NonNull
    private ArrayAdapter<String> getCountryProgramsAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.view_spinner_dropdown_white
                , getResources().getStringArray(R.array.country_programs));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    protected User getLoggedUser() {
        return user;
    }

    protected void setUser(User user){}

    protected Toolbar getToolbar() {
        return toolbar;
    }

    protected TabLayout getTabLayout() {
        return tabLayout;
    }

    public FloatingActionButton getMainActionButton() {
        return mainActionButton;
    }

    protected boolean hasTabLayout() {
        return true;
    }

    protected boolean hasMainActionButton() {
        return false;
    }

    protected NavigationView getMenuNavigation() {
        return menuNavigation;
    }

    protected AppBarLayout getAppBar() {
        return appBar;
    }

    protected void openEndDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public Loader<List<Notification>> onCreateLoader(int id, Bundle args) {
        return new NotificationLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Notification>> loader, List<Notification> data) {
        NotificationAdapter adapter = new NotificationAdapter(data);
        notificationsList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Notification>> loader) {}

    private View.OnClickListener onMenuHeaderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent profileIntent = new Intent(BaseActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        }
    };

    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            Intent navigationIntent;
            switch(menuItem.getItemId()) {
                case R.id.home:
                    navigationIntent = new Intent(BaseActivity.this, MainActivity.class);
                    break;
                case R.id.chat:
                    if(UserLoginManager.validateUserLogin(BaseActivity.this))
                        navigationIntent = new Intent(BaseActivity.this, ChatActivity.class);
                    else
                        return false;
                    break;
                case R.id.about:
                    navigationIntent = new Intent(BaseActivity.this, AboutActivity.class);
                    break;
                case R.id.makeDonations:
                    navigationIntent = new Intent(BaseActivity.this, DonationActivity.class);
                    break;
                case R.id.buyMerchandising:
                    openMerchandise(URL_UNICEF_SHOP);
                    return true;
                case R.id.ambassadors:
                    openMerchandise(URL_UNICEF_AMBASSADORS);
                    return true;
                default:
                    return true;
            }

            menuItem.setChecked(true);
            startActivity(navigationIntent);
            finish();
            return true;
        }
    };

    private void openMerchandise(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

}
