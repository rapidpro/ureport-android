package in.ureport.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.List;
import java.util.Locale;

import in.ureport.R;
import in.ureport.models.User;
import in.ureport.models.geonames.State;
import in.ureport.models.holders.UserGender;
import in.ureport.models.holders.UserLocale;
import in.ureport.models.rapidpro.Contact;
import in.ureport.network.UserServices;
import in.ureport.tasks.SaveContactTask;

/**
 * Created by johncordeiro on 10/09/15.
 */
public class EditUserFragment extends UserInfoBaseFragment {

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
    public void onStatesLoaded(List<State> states) {
        selectUserState(states, getUserState(states));
    }

    private void selectUserState(List<State> states, State userState) {
        int userStatePosition = states.indexOf(userState);
        if(userStatePosition >= 0) {
            state.setSelection(userStatePosition);
        }
    }

    private State getUserState(List<State> states) {
        for (State state : states) {
            if(hasUserState(state)) {
               return state;
            }
        }
        return null;
    }

    private boolean hasUserState(State state) {
        return state.getName().equals(user.getState()) || state.getToponymName().equals(user.getState());
    }

    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validFieldsForCustomUser()) {
                user.setNickname(username.getText().toString());
                user.setBirthday(getBirthdayDate());

                State state = (State) EditUserFragment.this.state.getSelectedItem();
                user.setState(state.getToponymName());

                UserGender gender = (UserGender) EditUserFragment.this.gender.getSelectedItem();
                user.setGender(gender.getGender());

                progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.load_message_wait), true, false);
                userServices.editUser(user, onUserUpdatedListener);
            }
        }
    };

    private Firebase.CompletionListener onUserUpdatedListener = new Firebase.CompletionListener() {
        @Override
        public void onComplete(final FirebaseError firebaseError, Firebase firebase) {
            progressDialog.dismiss();
            if(firebaseError == null) {
                updateContactToRapidpro();
            } else {
                displayError();
            }
        }

        private void updateContactToRapidpro() {
            SaveContactTask saveContactTask = new SaveContactTask(getActivity()) {
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
