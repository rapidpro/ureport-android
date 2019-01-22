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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import in.ureport.R;
import in.ureport.fragments.ChatsFragment;
import in.ureport.fragments.GeneralSettingsFragment;
import in.ureport.fragments.ProfileFragment;
import in.ureport.fragments.StoriesListFragment;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.LocalNotificationManager;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.network.UserServices;
import in.ureport.pref.SystemPreferences;
import in.ureport.tasks.SaveContactTask;
import io.rapidpro.sdk.FcmClient;
import io.rapidpro.sdk.core.models.base.ContactBase;

public class HomeActivity extends AppCompatActivity implements StoriesListFragment.OnPublishStoryListener {

    private static final int REQUEST_CODE_CREATE_STORY = 10;
    public static final int REQUEST_CODE_TUTORIAL = 201;

    private User user;
    private LocalNotificationManager localNotificationManager;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loadData();
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
    public void onPublishStory() {
        if (UserManager.validateKeyAction(this)) {
            Intent createStoryIntent = new Intent(this, CreateStoryActivity.class);
            startActivityForResult(createStoryIntent, REQUEST_CODE_CREATE_STORY);
        }
    }

    private void loadData() {
        if (!UserManager.isUserLoggedIn()) {
            return;
        }
        UserServices userServices = new UserServices();
        userServices.getUser(UserManager.getUserId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                HomeActivity.this.user = refreshUserCountry(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @NonNull
    private User refreshUserCountry(User user) {
        if (user == null) {
            user = new User();
            user.setCountry(Locale.getDefault().getDisplayCountry());
        }
        return user;
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
        bottomNavigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.main:
                    final StoriesListFragment fragment = new StoriesListFragment();
                    replaceContent(fragment);
                    fragment.updateUser(user);
                    break;
                case R.id.search:
                    break;
                case R.id.chat:
                    replaceContent(new ChatsFragment());
                    break;
                case R.id.profile:
                    replaceContent(new ProfileFragment());
                    break;
                case R.id.settings:
                    replaceContent(new GeneralSettingsFragment());
                    break;
            }
            return true;
        });
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

    private void replaceContent(final Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
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
