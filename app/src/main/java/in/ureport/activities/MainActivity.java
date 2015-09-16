package in.ureport.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.UreportApplication;
import in.ureport.fragments.ListChatRoomsFragment;
import in.ureport.fragments.PollQuestionFragment;
import in.ureport.fragments.PollsFragment;
import in.ureport.fragments.StoriesListFragment;
import in.ureport.listener.FloatingActionButtonListener;
import in.ureport.listener.OnSeeLastPollsListener;
import in.ureport.listener.OnSeeOpenGroupsListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.User;
import in.ureport.models.Notification;
import in.ureport.models.holders.NavigationItem;
import in.ureport.views.adapters.NavigationAdapter;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class MainActivity extends BaseActivity implements FloatingActionButtonListener
        , StoriesAdapter.OnPublishStoryListener, OnSeeOpenGroupsListener, OnSeeLastPollsListener {

    private static final int REQUEST_CODE_CREATE_STORY = 10;
    private static final int REQUEST_CODE_CHAT_CREATION = 200;
    public static final int REQUEST_CODE_CHAT_NOTIFICATION = 300;

    private static final int POSITION_STORIES_FRAGMENT = 0;
    private static final int POSITION_CHAT_FRAGMENT = 2;

    public static final String ACTION_OPEN_CHAT_NOTIFICATION = "in.ureport.ChatNotification";
    public static final String EXTRA_FORCED_LOGIN = "forcedLogin";

    private TextView notificationsAlert;
    private ViewPager pager;

    private StoriesListFragment storiesListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        final View notifications = MenuItemCompat.getActionView(menu.findItem(R.id.notifications));
        notifications.setOnClickListener(onNotificationsClickListener);
        notificationsAlert = (TextView) notifications.findViewById(R.id.notificationAlerts);
        onNotificationsLoaded(getNotificationAlerts());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifications:
                openEndDrawer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHAT_CREATION:
                    startChatRoom(data);
            }
        }
    }

    private void startChatRoom(Intent data) {
        ChatRoom chatRoom = data.getParcelableExtra(ChatCreationActivity.EXTRA_CHAT_ROOM);
        ChatMembers chatMembers = data.getParcelableExtra(ChatCreationActivity.EXTRA_CHAT_MEMBERS);

        if(chatRoom != null && chatMembers != null) {
            Intent chatRoomIntent = new Intent(this, ChatRoomActivity.class);
            chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM, chatRoom);
            chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_MEMBERS, chatMembers);
            startActivity(chatRoomIntent);
        }
    }

    private void setupView() {
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
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
        return position == POSITION_STORIES_FRAGMENT || position == POSITION_CHAT_FRAGMENT;
    }

    private void setupNavigationAdapter() {
        storiesListFragment = new StoriesListFragment();
        storiesListFragment.setFloatingActionButtonListener(this);
        storiesListFragment.setOnPublishStoryListener(this);
        NavigationItem storiesItem = new NavigationItem(storiesListFragment, getString(R.string.main_stories));

        NavigationItem pollsItem = getPollsNavigationItem();
        NavigationItem chatItem = new NavigationItem(new ListChatRoomsFragment(), getString(R.string.main_chat));

        NavigationItem [] navigationItems = {storiesItem, pollsItem, chatItem};
        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager()
                , navigationItems);

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(navigationItems.length);
        selectChatIfNeeded();
    }

    private void selectChatIfNeeded() {
        if(getIntent().getAction() != null
        && getIntent().getAction().equals(ACTION_OPEN_CHAT_NOTIFICATION)) {
            pager.setCurrentItem(POSITION_CHAT_FRAGMENT);
        }
    }

    @NonNull
    private NavigationItem getPollsNavigationItem() {
        if(UserManager.isUserCountryProgramEnabled()) {
            return new NavigationItem(new PollQuestionFragment(), getString(R.string.main_polls));
        } else {
            return new NavigationItem(new PollsFragment(), getString(R.string.main_polls));
        }
    }

    @Override
    public void setUser(User user) {
        super.setUser(user);

        if(user != null) {
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
            checkFloatingButtonAction(position);
        }
    };

    private void checkFloatingButtonAction(int position) {
        switch(position) {
            case POSITION_STORIES_FRAGMENT:
                getMainActionButton().setOnClickListener(onCreateStoryClickListener);
                break;
            case POSITION_CHAT_FRAGMENT:
                getMainActionButton().setOnClickListener(onCreateChatClickListener);
        }
    }

    private View.OnClickListener onCreateChatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            createChat();
        }
    };

    private void createChat() {
        if(UreportApplication.validateUserLogin(MainActivity.this)) {
            Intent newChatIntent = new Intent(MainActivity.this, ChatCreationActivity.class);
            startActivityForResult(newChatIntent, REQUEST_CODE_CHAT_CREATION);
        }
    }

    private View.OnClickListener onCreateStoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            publishStory();
        }
    };

    private View.OnClickListener onNotificationsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openEndDrawer();
        }
    };

    @Override
    protected void onNotificationsLoaded(List<Notification> notifications) {
        super.onNotificationsLoaded(notifications);
        if(notificationsAlert == null) return;

        if(notifications != null && notifications.size() > 0) {
            notificationsAlert.setVisibility(View.VISIBLE);
            notificationsAlert.setText(String.valueOf(notifications.size()));
        } else {
            notificationsAlert.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSeeOpenGroups() {
        Intent openGroupsIntent = new Intent(this, OpenGroupsActivity.class);
        startActivity(openGroupsIntent);
    }

    @Override
    public void onSeeLastPolls() {
        Intent seeLastPolls = new Intent(this, LastPollsActivity.class);
        startActivity(seeLastPolls);
    }
}
