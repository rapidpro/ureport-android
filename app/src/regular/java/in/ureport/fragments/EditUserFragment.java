package in.ureport.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.models.User;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import in.ureport.models.holders.UserGender;
import in.ureport.network.UserServices;
import in.ureport.tasks.SaveContactTask;
import io.rapidpro.sdk.core.models.base.ContactBase;

/**
 * Created by johncordeiro on 10/09/15.
 */
public class EditUserFragment extends UserInfoBaseFragment {

    private static final String TAG = "EditUserFragment";

    private UserSettingsFragment.UserSettingsListener userSettingsListener;

    private static DatabaseReference.CompletionListener firebaseCompletionListener;
    private static SaveContactTask.Listener saveContactTaskListener;

    private CircleImageView photo;
    private TextView changePhoto;

    public static EditUserFragment newInstance(User user) {
        EditUserFragment editUserFragment = new EditUserFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        editUserFragment.setArguments(args);
        return editUserFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupContextDependencies();
        setupView(view);
        setLoadingMessage(getString(R.string.load_message_save_user));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof UserSettingsFragment.UserSettingsListener) {
            userSettingsListener = (UserSettingsFragment.UserSettingsListener) context;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_profile, menu);
    }

    private void setupContextDependencies() {
        firebaseCompletionListener = ((error, reference) -> {
            dismissLoading();
            if (error == null) {
                updateContactToRapidpro(getContext(), user, getCountrySelected());
            } else {
                displayError();
            }
        });
        saveContactTaskListener = new SaveContactTask.Listener() {
            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onFinished(ContactBase contact, User user) {
                dismissLoading();
                if (contact != null) {
                    userSettingsListener.onEditFinished();
                } else {
                    displayError();
                }
            }
        };
    }

    private void setupView(View view) {
        final AppCompatActivity activity = (AppCompatActivity) requireActivity();
        final ActionBar actionBar = activity.getSupportActionBar();
        activity.setSupportActionBar(toolbar);

        if (actionBar != null) {
            final Drawable indicator = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(indicator);
        }
        toolbar.setTitle(R.string.title_pref_edit_profile);
        toolbar.setTitleTextColor(ContextCompat.getColor(activity, android.R.color.black));

        photo = view.findViewById(R.id.photo);
        changePhoto = view.findViewById(R.id.changePhoto);
        view.findViewById(R.id.line).setVisibility(View.VISIBLE);

        photo.setVisibility(View.VISIBLE);
        changePhoto.setVisibility(View.VISIBLE);
        email.setEnabled(false);
        password.setText("password");
        view.findViewById(R.id.line).setVisibility(View.VISIBLE);
        view.findViewById(R.id.privateInformation).setVisibility(View.VISIBLE);
        view.findViewById(R.id.deleteAccount).setVisibility(View.VISIBLE);
        confirm.setVisibility(View.GONE);

        updateLayoutConstraints(view);
        ImageLoader.loadPersonPictureToImageView(photo, user.getPicture());
    }

    private void updateLayoutConstraints(View parent) {
        final ConstraintLayout content = parent.findViewById(R.id.content);
        final ConstraintSet set = new ConstraintSet();
        set.clone(content);

        set.clear(R.id.emailLayout, ConstraintSet.TOP);
        set.clear(R.id.birthdayLayout, ConstraintSet.TOP);
        set.clear(R.id.gender, ConstraintSet.TOP);
        set.clear(R.id.line, ConstraintSet.TOP);
        set.clear(R.id.deleteAccount, ConstraintSet.TOP);

        set.connect(R.id.emailLayout, ConstraintSet.TOP, R.id.privateInformation, ConstraintSet.BOTTOM, dpsToPixels(32));
        set.connect(R.id.birthdayLayout, ConstraintSet.TOP, R.id.nameLayout, ConstraintSet.BOTTOM, dpsToPixels(16));
        set.connect(R.id.gender, ConstraintSet.TOP, R.id.passwordLayout, ConstraintSet.BOTTOM, dpsToPixels(16));
        set.connect(R.id.line, ConstraintSet.TOP, R.id.district, ConstraintSet.BOTTOM, dpsToPixels(24));
        set.connect(R.id.deleteAccount, ConstraintSet.TOP, R.id.gender, ConstraintSet.BOTTOM, dpsToPixels(32));

        set.applyTo(content);
    }

    private int dpsToPixels(int dps) {
        final float density = getResources().getDisplayMetrics().density;
        return Math.round(dps * density);
    }

    @Override
    public void onCountriesLoaded(List<CountryInfo> data) {
        country.setEnabled(false);
        selectUserCountry(data, getUserCountry(data));
    }

    @Nullable
    private CountryInfo getUserCountry(List<CountryInfo> data) {
        for (CountryInfo countryInfo : data) {
            try {
                if (hasUserISOCode(countryInfo.getIsoAlpha3())) {
                    return countryInfo;
                }
            } catch(Exception ignored){}
        }
        return null;
    }

    private boolean hasUserISOCode(String countryCode) {
        return countryCode != null && countryCode.equals(user.getCountry());
    }

    private void selectUserCountry(List<CountryInfo> data, CountryInfo countryInfo) {
        int countryInfoPosition = data.indexOf(countryInfo);

        if(countryInfoPosition >= 0) {
            country.setSelection(countryInfoPosition);
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

    public static void updateContactToRapidpro(Context context, User user, CountryInfo countryInfo) {
        new SaveContactTask(context, user, false, countryInfo) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                saveContactTaskListener.onStart();
            }

            @Override
            protected void onPostExecute(ContactBase contact) {
                super.onPostExecute(contact);
                saveContactTaskListener.onFinished(contact, user);
            }
        }.execute();
    }

    private View.OnClickListener onConfirmClickListener = view -> {
        if (validFieldsForCustomUser()) {
            user.setNickname(username.getText().toString());
            user.setBirthday(getBirthdayDate().getTime());

            Location location = (Location) EditUserFragment.this.state.getSelectedItem();
            user.setState(location.getName());

            if (containsDistrict) {
                Location district = (Location) EditUserFragment.this.district.getSelectedItem();
                user.setDistrict(district.getName());
            }

            UserGender gender = (UserGender) EditUserFragment.this.gender.getSelectedItem();
            user.setGenderAsEnum(gender.getGender());

            showLoading();
            UserServices userServices = new UserServices();
            userServices.editUser(user, (firebaseError, firebase) ->
                    firebaseCompletionListener.onComplete(firebaseError, firebase)
            );
        }
    };

    private void displayError() {
        Toast.makeText(getContext(), R.string.error_update_user, Toast.LENGTH_SHORT).show();
    }

    private boolean validFieldsForCustomUser() {
        return isUsernameValid() && isBirthdayValid() && isStateValid() && isSpinnerValid(gender);
    }

}