package in.ureport.fragments;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import in.ureport.R;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.network.UserServices;

/**
 * Created by johncordeiro on 18/09/15.
 */
public class GeneralSettingsFragment extends PreferenceFragmentCompat {

    private static final String PUBLIC_PROFILE_KEY = "pref_key_chat_available";
    private static final String CHAT_NOTIFICATIONS_KEY = "pref_key_chat_notifications";

    private UserServices userServices;

    private SwitchPreferenceCompat publicProfilePreference;
    private User user;

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.general_settings_preferences, rootKey);

        setupView();
        setupObjects();
        loadUser();
    }

    private void setupView() {
        publicProfilePreference = (SwitchPreferenceCompat)getPreferenceManager().findPreference(PUBLIC_PROFILE_KEY);
        publicProfilePreference.setOnPreferenceChangeListener(onPublicProfilePreferenceChangeListener);
    }

    private void setupObjects() {
        userServices = new UserServices();
    }

    private void loadUser() {
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                user = dataSnapshot.getValue(User.class);
                if (user != null) updateViewForUser(user);
            }
        });
    }

    private void updateViewForUser(User user) {
        publicProfilePreference.setChecked(user.getPublicProfile());
    }

    private Preference.OnPreferenceChangeListener onPublicProfilePreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object object) {
            publicProfilePreference.setChecked(!publicProfilePreference.isChecked());
            if(user != null) {
                userServices.changePublicProfile(user, publicProfilePreference.isChecked(), onSettingsSavedListener);
            } else {
                displayMessage(R.string.error_update_user);
            }
            return false;
        }
    };

    private Firebase.CompletionListener onSettingsSavedListener = new Firebase.CompletionListener() {
        @Override
        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            if(firebaseError == null) {
                displayMessage(R.string.message_success_user_update);
            } else {
                displayMessage(R.string.error_update_user);
            }
        }
    };

    private void displayMessage(int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
