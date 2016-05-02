package in.ureport.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.ureport.R;
import in.ureport.loader.NotificationLoader;
import in.ureport.managers.CountryProgramManager;
import in.ureport.helpers.ImageLoader;
import in.ureport.managers.DonationManager;
import in.ureport.managers.PrototypeManager;
import in.ureport.helpers.SpinnerColorSwitcher;
import in.ureport.managers.UserManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.Notification;
import in.ureport.models.User;
import in.ureport.network.UserServices;
import in.ureport.helpers.DividerItemDecoration;
import in.ureport.views.adapters.NotificationAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public abstract class BaseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Notification>> {

    public static final String ACTION_RELOAD_NOTIFICATIONS = "in.ureport.ReloadNotifications";

    private AppBarLayout appBar;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private NavigationView menuNavigation;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;

    private RecyclerView notificationsList;
    private Spinner countryPrograms;

    private User user;
    private List<Notification> notificationAlerts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        super.setContentView(R.layout.activity_base);
        setupBaseView();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reloadNotificationsReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void setupBaseView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appBar = (AppBarLayout) findViewById(R.id.appbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setVisibility(hasTabLayout() ? View.VISIBLE : View.GONE);

        menuNavigation = (NavigationView) findViewById(R.id.menuNavigation);
        menuNavigation.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        setupMenuPermissions();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout
                , toolbar, R.string.base_menu_open_drawer, R.string.base_menu_close_drawer);

        notificationsList = (RecyclerView) findViewById(R.id.notificationsList);
        notificationsList.setLayoutManager(new LinearLayoutManager(this));
        notificationsList.addItemDecoration(new DividerItemDecoration(this));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        registerNotificationListener();
    }

    private void registerNotificationListener() {
        IntentFilter intentFilter = new IntentFilter(ACTION_RELOAD_NOTIFICATIONS);
        registerReceiver(reloadNotificationsReceiver, intentFilter);
    }

    private void setupMenuPermissions() {
        Menu menu = getMenuNavigation().getMenu();
        menu.findItem(R.id.moderation).setVisible(UserManager.isUserLoggedIn() && UserManager.canModerate());
        menu.findItem(R.id.changeSettings).setVisible(UserManager.isUserLoggedIn());
        menu.findItem(R.id.logout).setVisible(UserManager.isUserLoggedIn());
        menu.findItem(R.id.makeDonation).setVisible(user != null && DonationManager.isDonationAllowed(user));
        onMenuLoaded();
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
        if(UserManager.isUserLoggedIn()) {
            UserServices userServices = new UserServices();
            userServices.getUser(UserManager.getUserId(), new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    updateUserInfo(user);
                    user = refreshUserCountry(user);
                    setUser(user);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });
        }
    }

    private void loadNotifications() {
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

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
            menuNavigation.getMenu().clear();
            menuNavigation.inflateMenu(R.menu.main);
            setupMenuPermissions();

            View menuHeader = getLayoutInflater().inflate(R.layout.view_header_menu, null);
            menuHeader.setOnClickListener(onMenuHeaderClickListener);

            ImageView picture = (ImageView) menuHeader.findViewById(R.id.picture);
            ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());

            TextView name = (TextView) menuHeader.findViewById(R.id.name);
            name.setText(user.getNickname());

            TextView points = (TextView) menuHeader.findViewById(R.id.points);
            points.setText(getString(R.string.menu_points, getIntegerValue(user.getPoints())));

            TextView stories = (TextView) menuHeader.findViewById(R.id.stories);
            stories.setText(getString(R.string.profile_stories, getIntegerValue(user.getStories())));

            List<CountryProgram> countryProgramList = new ArrayList<>(CountryProgramManager.getAvailableCountryPrograms());

            countryPrograms = (Spinner) menuHeader.findViewById(R.id.countryPrograms);
            countryPrograms.setAdapter(getCountryProgramsAdapter(countryProgramList));
            countryPrograms.setTag(R.id.country_program_position, 0);
            countryPrograms.setOnItemSelectedListener(onCountryProgramClickListener);

            SpinnerColorSwitcher spinnerColorSwitcher = new SpinnerColorSwitcher(this);
            spinnerColorSwitcher.switchToColor(countryPrograms, android.R.color.white);

            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            coordinatorLayout.setOnTouchListener(onCoordinatorLayoutTouchListener);

            menuNavigation.addHeaderView(menuHeader);
        }
    }

    private int getIntegerValue(Integer value) {
        return value != null ? value : 0;
    }

    @NonNull
    private ArrayAdapter<CountryProgram> getCountryProgramsAdapter(List<CountryProgram> countryPrograms) {
        countryPrograms.add(0, new CountryProgram("NONE", getString(R.string.switch_country_program)));

        ArrayAdapter<CountryProgram> adapter = new ArrayAdapter<>(this, R.layout.view_spinner_dropdown_white
                , countryPrograms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    protected User getLoggedUser() {
        return user;
    }

    protected void setUser(User user){}

    protected void onMenuLoaded(){}

    protected Toolbar getToolbar() {
        return toolbar;
    }

    protected TabLayout getTabLayout() {
        return tabLayout;
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
        this.notificationAlerts = data;
        onNotificationsLoaded(data);

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
                    menuItem.setChecked(true);
                    navigationIntent = new Intent(BaseActivity.this, MainActivity.class);
                    break;
                case R.id.moderation:
                    menuItem.setChecked(true);
                    navigationIntent = new Intent(BaseActivity.this, ModerationActivity.class);
                    break;
                case R.id.about:
                    navigationIntent = new Intent(BaseActivity.this, AboutActivity.class);
                    startActivity(navigationIntent);
                    return true;
                case R.id.makeDonation:
                    navigationIntent = new Intent(BaseActivity.this, DonationActivity.class);
                    navigationIntent.putExtra(DonationActivity.EXTRA_USER, user);
                    startActivity(navigationIntent);
                    return true;
                case R.id.changeSettings:
                    navigationIntent = new Intent(BaseActivity.this, GeneralSettingsActivity.class);
                    startActivity(navigationIntent);
                    return true;
                case R.id.logout:
                    UserManager.logout(BaseActivity.this);
                    UserManager.startLoginFlow(BaseActivity.this);
                    finish();
                    return true;
                default:
                    PrototypeManager.showPrototypeAlert(BaseActivity.this);
                    return true;
            }

            startActivity(navigationIntent);
            finish();
            return true;
        }
    };

    private void restartActivity() {
        Intent mainIntent = new Intent(BaseActivity.this, getClass());
        startActivity(mainIntent);
        finish();
    }

    private View.OnTouchListener onCoordinatorLayoutTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            return true;
        }
    };

    private AdapterView.OnItemSelectedListener onCountryProgramClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            Object tag = countryPrograms.getTag(R.id.country_program_position);
            if(tag != null && !tag.equals(position) && position > 0) {
                view.setTag(R.id.country_program_position, position);

                CountryProgram countryProgram = (CountryProgram) adapterView.getAdapter().getItem(position);
                CountryProgramManager.switchCountryProgram(countryProgram);
                restartActivity();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    };

    protected void onNotificationsLoaded(List<Notification> notifications) {}

    protected List<Notification> getNotificationAlerts() {
        return notificationAlerts;
    }

    private BroadcastReceiver reloadNotificationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadNotifications();
        }
    };
}
