package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import in.ureport.R;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 10/09/15.
 */
public class UserSettingsFragment extends PreferenceFragmentCompat {

    public static final String EDIT_PROFILE_KEY = "pref_key_edit_profile";
    public static final String CHANGE_PASSWORD_KEY = "pref_key_change_password";

    private static final String EXTRA_USER = "user";

    private UserSettingsListener userSettingsListener;

    private User user;

    public static UserSettingsFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        UserSettingsFragment fragment = new UserSettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getUserByArguments() {
        if(getArguments() != null && getArguments().containsKey(EXTRA_USER)) {
            user = getArguments().getParcelable(EXTRA_USER);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.user_settings_preferences);

        getUserByArguments();
        setChangePasswordVisibility();
    }

    private void setChangePasswordVisibility() {
        Preference changePasswordPreference = getPreferenceManager().findPreference(CHANGE_PASSWORD_KEY);
        changePasswordPreference.setVisible(user.getType() == User.Type.ureport);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof UserSettingsListener) {
            userSettingsListener = (UserSettingsListener) context;
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case EDIT_PROFILE_KEY:
                userSettingsListener.onEditProfile();
                break;
            case CHANGE_PASSWORD_KEY:
                userSettingsListener.onChangePassword();
        }
        return super.onPreferenceTreeClick(preference);
    }

    public interface UserSettingsListener {
        void onEditProfile();
        void onChangePassword();
        void onEditFinished();
    }
}
