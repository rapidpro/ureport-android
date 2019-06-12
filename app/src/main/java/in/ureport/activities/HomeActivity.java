package in.ureport.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import in.ureport.R;
import in.ureport.fragments.ChatsFragment;
import in.ureport.fragments.GeneralSettingsFragment;
import in.ureport.fragments.HomeFragment;
import in.ureport.fragments.ProfileFragment;
import in.ureport.fragments.SettingsFragment;
import in.ureport.fragments.StoriesListFragment;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnUserStartChattingListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.LocalNotificationManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.User;
import in.ureport.models.holders.ChatRoomHolder;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.pref.SystemPreferences;
import in.ureport.tasks.SaveContactTask;
import io.rapidpro.sdk.FcmClient;
import io.rapidpro.sdk.core.models.base.ContactBase;

public class HomeActivity extends AppCompatActivity
        implements StoriesListFragment.OnPublishStoryListener, OnUserStartChattingListener {

    private static final int REQUEST_CODE_CREATE_STORY = 10;
    public static final int REQUEST_CODE_CHAT_CREATION = 200;
    public static final int REQUEST_CODE_TUTORIAL = 201;

    private LocalNotificationManager localNotificationManager;

    private int roomMembersLoaded = 0;
    private boolean chatRoomFound = false;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_home);
        setupObjects();
        checkTutorialView();
        setupView();
        checkUserRegistration();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHAT_CREATION:
                    startChatRoom(data);
                    break;
                case REQUEST_CODE_TUTORIAL:
                    FcmClient.requestFloatingPermissionsIfNeeded(this);
            }
        }
    }

    @Override
    public void onPublishStory() {
        if (UserManager.validateKeyAction(this)) {
            Intent createStoryIntent = new Intent(this, CreateStoryActivity.class);
            startActivityForResult(createStoryIntent, REQUEST_CODE_CREATE_STORY);
        }
    }

    @Override
    public void onUserStartChatting(User user) {
        if (UserManager.validateKeyAction(this)) {
            UserServices userServices = new UserServices();

            userServices.loadChatRooms(UserManager.getUserId(), new ValueEventListenerAdapter() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
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

                if (chatMembers.getUsers().size() == 2 && chatMembers.getUsers().contains(user)) {
                    chatRoomFound = true;
                    ChatsFragment.startChatRoom(this, new ChatRoom(chatRoom.getKey()));
                } else if (needsChatCreation) {
                    createChat(user);
                }
            });
        }
    }

    private void createChat(User user) {
        if (UserManager.validateKeyAction(this)) {
            Intent newChatIntent = new Intent(this, ChatCreationActivity.class);
            newChatIntent.putParcelableArrayListExtra(ChatCreationActivity.EXTRA_CHAT_ROOMS,
                    new ArrayList<ChatRoomHolder>());
            if (user != null) newChatIntent.putExtra(ChatCreationActivity.EXTRA_USER, user);
            startActivityForResult(newChatIntent, REQUEST_CODE_CHAT_CREATION);
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

    private void setupObjects() {
        localNotificationManager = new LocalNotificationManager(this);
    }

    private void checkTutorialView() {
        SystemPreferences systemPreferences = new SystemPreferences(this);
        if (!systemPreferences.getTutorialView()) {
            Intent tutorialViewIntent = new Intent(this, TutorialActivity.class);
            startActivityForResult(tutorialViewIntent, REQUEST_CODE_TUTORIAL);
        } else {
            FcmClient.requestFloatingPermissionsIfNeeded(this);
        }
    }

    private void setupView() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        if (!UserManager.isUserLoggedIn()) {
            bottomNavigation.setVisibility(View.GONE);
            switchToHome();
            return;
        }

        bottomNavigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.main:
                    switchToHome();
                    break;
                case R.id.search:
                    break;
                case R.id.chats:
                    switchToChats();
                    break;
                case R.id.profile:
                    switchToProfile();
                    break;
                case R.id.settings:
                    switchToSettings();
                    break;
            }
            return true;
        });
        switchToHome();
    }

    private void checkUserRegistration() {
        if (FcmClient.isContactRegistered() || UserManager.getUserId() == null) {
            return;
        }
        UserServices userServices = new UserServices();
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                User user = dataSnapshot.getValue(User.class);
                saveContact(user);
            }
        });
    }

    private void switchToHome() {
        if (isVisibleFragment(HomeFragment.TAG)) {
            return;
        }
        replaceContent(new HomeFragment(), HomeFragment.TAG);
    }

    private void switchToChats() {
        if (isVisibleFragment(ChatsFragment.TAG)) {
            return;
        }
        replaceContent(new ChatsFragment(), ChatsFragment.TAG);
    }

    private void switchToProfile() {
        if (isVisibleFragment(ProfileFragment.TAG)) {
            return;
        }
        replaceContent(new ProfileFragment(), ProfileFragment.TAG);
    }

    private void switchToSettings() {
        if (isVisibleFragment(SettingsFragment.TAG)) {
            return;
        }
        replaceContent(SettingsFragment.newInstance(), GeneralSettingsFragment.TAG);
    }

    private void replaceContent(final Fragment fragment, final String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    private boolean isVisibleFragment(final String tag) {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        return fragment != null && fragment.isVisible();
    }

    private void saveContact(final User user) {
        new SaveContactTask(this, user, false) {
            @Override
            protected void onPostExecute(ContactBase contact) {
                super.onPostExecute(contact);
                if (contact != null && !TextUtils.isEmpty(contact.getUuid())) {
                    UserServices userServices = new UserServices();
                    userServices.saveUserContactUuid(user, contact.getUuid());
                }
            }
        }.execute();
    }

}
