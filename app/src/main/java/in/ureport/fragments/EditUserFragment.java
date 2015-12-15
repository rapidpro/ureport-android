package in.ureport.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.List;
import java.util.Locale;

import in.ureport.R;
import in.ureport.models.User;
import in.ureport.models.geonames.Location;
import in.ureport.models.holders.UserGender;
import in.ureport.models.holders.UserLocale;
import in.ureport.flowrunner.models.Contact;
import in.ureport.network.UserServices;
import in.ureport.tasks.SaveContactTask;

/**
 * Created by johncordeiro on 10/09/15.
 */
public class EditUserFragment extends UserInfoBaseFragment {

    private static final String TAG = "EditUserFragment";

    private UserServices userServices;

    private UserSettingsFragment.UserSettingsListener userSettingsListener;

    private ProgressDialog progressDialog;

    public static EditUserFragment newInstance(User user) {
        EditUserFragment editUserFragment = new EditUserFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        editUserFragment.setArguments(args);
        return editUserFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObjects();
        setupView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof UserSettingsFragment.UserSettingsListener) {
            userSettingsListener = (UserSettingsFragment.UserSettingsListener) context;
        }
    }

    private void setupView() {
        password.setVisibility(View.GONE);
        country.setEnabled(false);
        email.setVisibility(View.GONE);

        confirm.setOnClickListener(onConfirmClickListener);
    }

    private void setupObjects() {
        userServices = new UserServices();
    }

    @Override
    public void onCountriesLoaded(List<UserLocale> data) {
        selectUserCountry(data, getUserLocale(data));
    }

    @Nullable
    private UserLocale getUserLocale(List<UserLocale> data) {
        for (UserLocale userLocale : data) {
            try {
                if (hasUserISOCode(userLocale.getLocale())) {
                    return userLocale;
                }
            } catch(Exception ignored){}
        }
        return null;
    }

    private boolean hasUserISOCode(Locale locale) {
        return locale.getDisplayCountry() != null && locale.getISO3Country() != null
                && locale.getISO3Country().equals(user.getCountry());
    }

    private void selectUserCountry(List<UserLocale> data, UserLocale locale) {
        int userLocalePosition = data.indexOf(locale);

        if(userLocalePosition >= 0) {
            country.setSelection(userLocalePosition);
        }
    }

    @Override
    public void onStatesLoaded(List<Location> locations) {
        selectUserState(locations, getUserState(locations));
    }

    private void selectUserState(List<Location> locations, Location userLocation) {
        int userStatePosition = locations.indexOf(userLocation);
        if(userStatePosition >= 0) {
            state.setSelection(userStatePosition);
        }
    }

    private Location getUserState(List<Location> locations) {
        for (Location location : locations) {
            if(hasUserState(location)) {
               return location;
            }
        }
        return null;
    }

    private boolean hasUserState(Location location) {
        return location.getName().equals(user.getState()) || location.getToponymName().equals(user.getState());
    }

    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validFieldsForCustomUser()) {
                user.setNickname(username.getText().toString());
                user.setBirthday(getBirthdayDate());

                Location location = (Location) EditUserFragment.this.state.getSelectedItem();
                user.setState(location.getName());

                if(containsDistrict) {
                    Location district = (Location) EditUserFragment.this.district.getSelectedItem();
                    user.setDistrict(district.getName());
                }

                UserGender gender = (UserGender) EditUserFragment.this.gender.getSelectedItem();
                user.setGender(gender.getGender());

                progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.load_message_wait), true, false);
                Log.i(TAG, "onClick editUser ");
                userServices.editUser(user, onUserUpdatedListener);
            }
        }
    };

    private Firebase.CompletionListener onUserUpdatedListener = new Firebase.CompletionListener() {
        @Override
        public void onComplete(final FirebaseError firebaseError, Firebase firebase) {
            Log.i(TAG, "onComplete editUser");
            progressDialog.dismiss();
            if(firebaseError == null) {
                updateContactToRapidpro();
            } else {
                displayError();
            }
        }

        private void updateContactToRapidpro() {
            SaveContactTask saveContactTask = new SaveContactTask(getActivity(), getUserLocale().getLocale()) {
                @Override
                protected void onPostExecute(Contact contact) {
                    super.onPostExecute(contact);
                    if(contact != null) {
                        userSettingsListener.onEditFinished();
                    } else {
                        displayError();
                    }
                }
            };
            saveContactTask.execute(user);
        }
    };

    private void displayError() {
        Toast.makeText(getActivity(), R.string.error_update_user, Toast.LENGTH_SHORT).show();
    }

    private boolean validFieldsForCustomUser() {
        return isUsernameValid() && isBirthdayValid() && isStateValid() && isSpinnerValid(gender);
    }
}
