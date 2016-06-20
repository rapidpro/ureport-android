package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.fragments.ChatsFragment;
import in.ureport.fragments.PollsFragment;
import in.ureport.fragments.StoriesListFragment;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnSeeOpenGroupsListener;
import in.ureport.listener.OnUserStartChattingListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.LocalNotificationManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.models.Notification;
import in.ureport.models.holders.ChatRoomHolder;
import in.ureport.models.holders.NavigationItem;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.pref.SystemPreferences;
import in.ureport.views.adapters.NavigationAdapter;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class MainActivity extends BaseActivity implements OnSeeOpenGroupsListener, OnUserStartChattingListener,
        StoriesListFragment.OnPublishStoryListener {

    private static final int REQUEST_CODE_CREATE_STORY = 10;
    public static final int REQUEST_CODE_CHAT_CREATION = 200;
    public static final int REQUEST_CODE_CHAT_NOTIFICATION = 300;
    public static final int REQUEST_CODE_MESSAGE_NOTIFICATION = 400;
    public static final int REQUEST_CODE_CONTRIBUTION_NOTIFICATION = 500;

    private static final int POSITION_POLLS_FRAGMENT = 1;
    private static final int POSITION_CHAT_FRAGMENT = 2;

    public static final String ACTION_CONTRIBUTION_NOTIFICATION = "in.ureport.ContributionNotification";
    public static final String ACTION_OPEN_CHAT_NOTIFICATION = "in.ureport.ChatNotification";
    public static final String ACTION_OPEN_MESSAGE_NOTIFICATION = "in.ureport.MessageNotification";
    public static final String ACTION_START_CHATTING = "in.ureport.StartChatting";

    public static final String EXTRA_FORCED_LOGIN = "forcedLogin";
    public static final String EXTRA_STORY = "story";
    public static final String EXTRA_USER = "user";

    private static final int LOAD_CHAT_TIME = 1000;

    private TextView notificationsAlert;
    private ViewPager pager;

    private StoriesListFragment storiesListFragment;
    private ChatsFragment chatsFragment;

    private LocalNotificationManager localNotificationManager;
    private Story story;

    private int roomMembersLoaded = 0;
    private boolean chatRoomFound = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupObjects();
        checkTutorialView();
        checkForcedLogin();
        setContentView(R.layout.activity_main);
        setupView();
        checkIntentAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        localNotificationManager.cancelContributionNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        localNotificationManager.cancelContributionNotification();
    }

    private void setupObjects() {
        localNotificationManager = new LocalNotificationManager(this);
    }

    private void checkTutorialView() {
        SystemPreferences systemPreferences = new SystemPreferences(this);
        if(!systemPreferences.getTutorialView()) {
            Intent tutorialViewIntent = new Intent(this, TutorialActivity.class);
            startActivity(tutorialViewIntent);
        }
    }

    private void checkForcedLogin() {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if(extras.containsKey(EXTRA_FORCED_LOGIN)) {
                Boolean forcedLogin = extras.getBoolean(EXTRA_FORCED_LOGIN, false);

                if(forcedLogin) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
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
        ChatRoom chatRoom = data.getParcelableExtra(ChatCreationActivity.EXTRA_RESULT_CHAT_ROOM);
        ChatMembers chatMembers = data.getParcelableExtra(ChatCreationActivity.EXTRA_RESULT_CHAT_MEMBERS);

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
        setupNavigationAdapter();

        getTabLayout().setupWithViewPager(pager);
    }

    @Override
    protected void onMenuLoaded() {
        super.onMenuLoaded();
        getMenuNavigation().getMenu().findItem(R.id.home).setChecked(true);
    }

    private void setupNavigationAdapter() {
        NavigationItem[] navigationItems = getNavigationItems();

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager()
                , navigationItems);

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(navigationItems.length);
    }

    @NonNull
    private NavigationItem[] getNavigationItems() {
        storiesListFragment = new StoriesListFragment();
        NavigationItem storiesItem = new NavigationItem(storiesListFragment, getString(R.string.main_stories));
        NavigationItem pollsItem = new NavigationItem(new PollsFragment(), getString(R.string.main_polls));

        NavigationItem [] navigationItems;
        if(UserManager.isUserLoggedIn() && (UserManager.isUserCountryProgramEnabled() || UserManager.isMaster())) {
            chatsFragment = new ChatsFragment();
            NavigationItem chatItem = new NavigationItem(chatsFragment, getString(R.string.main_chat));
            navigationItems = new NavigationItem[]{storiesItem, pollsItem, chatItem};
        } else {
            navigationItems = new NavigationItem[]{storiesItem, pollsItem};
        }
        return navigationItems;
    }

    private void checkIntentAction() {
        final String action = getIntent().getAction();
        if(action != null) {
            switch(action) {
                case ACTION_START_CHATTING:
                    pager.postDelayed(() -> {
                        User user1 = getIntent().getParcelableExtra(EXTRA_USER);
                        onUserStartChatting(user1);
                    }, LOAD_CHAT_TIME);
                    break;
                case ACTION_OPEN_CHAT_NOTIFICATION:
                    pager.setCurrentItem(POSITION_CHAT_FRAGMENT);
                    break;
                case ACTION_OPEN_MESSAGE_NOTIFICATION:
                    pager.setCurrentItem(POSITION_POLLS_FRAGMENT);
                    break;
                case ACTION_CONTRIBUTION_NOTIFICATION:
                    story = getIntent().getParcelableExtra(EXTRA_STORY);
            }
        }
    }

    @Override
    public void setUser(final User user) {
        super.setUser(user);

        if(user != null) {
            storiesListFragment.updateUser(user);
            getToolbar().setTitle(CountryProgramManager.getCurrentCountryProgram().getName());
            openStoryIfNeeded(user);
        }
    }

    private void openStoryIfNeeded(User user) {
        if(story != null) {
            startStoryViewActivity(story, user);
        }
    }

    private void startStoryViewActivity(Story story, User user) {
        Intent storyViewIntent = new Intent(MainActivity.this, StoryViewActivity.class);
        storyViewIntent.setAction(StoryViewActivity.ACTION_LOAD_STORY);
        storyViewIntent.putExtra(StoryViewActivity.EXTRA_STORY, story);
        storyViewIntent.putExtra(StoryViewActivity.EXTRA_USER, user);
        startActivity(storyViewIntent);
    }

    private void createChat(User user) {
        if(UserManager.validateKeyAction(MainActivity.this) && chatsFragment != null) {
            Intent newChatIntent = new Intent(MainActivity.this, ChatCreationActivity.class);
            newChatIntent.putParcelableArrayListExtra(ChatCreationActivity.EXTRA_CHAT_ROOMS
                    , (ArrayList<ChatRoomHolder>) chatsFragment.getChatRooms());
            if(user != null)  newChatIntent.putExtra(ChatCreationActivity.EXTRA_USER, user);
            startActivityForResult(newChatIntent, REQUEST_CODE_CHAT_CREATION);
        }
    }

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
    public void onUserStartChatting(final User user) {
        if(UserManager.validateKeyAction(this)) {
            UserServices userServices = new UserServices();

            userServices.loadChatRooms(UserManager.getUserId(), new ValueEventListenerAdapter() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    super.onDataChange(dataSnapshot);
                    roomMembersLoaded = 0;
                    chatRoomFound = false;
                    searchChatRoomWithUserOrCreate(dataSnapshot, user);
                }
            });
        }
    }

    private void searchChatRoomWithUserOrCreate(final DataSnapshot chatRoomsSnapshot, final User user) {
        final ChatRoomServices chatRoomServices = new ChatRoomServices();

        for (final DataSnapshot chatRoom : chatRoomsSnapshot.getChildren()) {
            chatRoomServices.loadChatRoomMembers(chatRoom.getKey(), chatMembers -> {
                roomMembersLoaded++;
                boolean needsChatCreation = !chatRoomFound && roomMembersLoaded >= chatRoomsSnapshot.getChildrenCount();

                if(chatMembers.getUsers().size() == 2
                && chatMembers.getUsers().contains(user)) {
                    chatRoomFound = true;
                    chatsFragment.startChatRoom(new ChatRoom(chatRoom.getKey()));
                } else if(needsChatCreation) {
                    createChat(user);
                }
            });
        }
    }

    @Override
    public void onPublishStory() {
        if(UserManager.validateKeyAction(MainActivity.this)) {
            Intent createStoryIntent = new Intent(MainActivity.this, CreateStoryActivity.class);
            startActivityForResult(createStoryIntent, REQUEST_CODE_CREATE_STORY);
        }
    }
}
