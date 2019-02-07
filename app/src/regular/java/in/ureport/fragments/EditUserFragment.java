package in.ureport.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.helpers.MediaSelector;
import in.ureport.helpers.TransferListenerAdapter;
import in.ureport.managers.TransferManager;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
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

    private static TransferListenerAdapter firebaseImageTransferListenerAdapter;
    private static DatabaseReference.CompletionListener firebaseCompletionListener;
    private static SaveContactTask.Listener saveContactTaskListener;

    private CircleImageView photo;
    private TextView changePhoto;

    private UserServices userServices;
    private MediaSelector mediaSelector;

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
        setupObjects();
        setupView(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof UserSettingsFragment.UserSettingsListener) {
            userSettingsListener = (UserSettingsFragment.UserSettingsListener) context;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaSelector.onActivityResult(this, onLoadLocalMediaListener, requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            onUpdateUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupContextDependencies() {
        firebaseImageTransferListenerAdapter = new TransferListenerAdapter(getContext(), null) {
            @Override
            public void onStart() {
                super.onStart();
                setLoadingMessage(getString(R.string.load_message_uploading_image));
                showLoading();
            }

            @Override
            public void onTransferFinished(Media media) {
                super.onTransferFinished(media);
                dismissLoading();
                if (user == null)
                    return;

                showLoading();
                user.setPicture(media.getUrl());
                userServices.editUserPicture(user, (firebaseError, firebase) -> {
                    dismissLoading();
                    if (firebaseError == null)
                        ImageLoader.loadPersonPictureToImageView(photo, media.getUrl());
                    else
                        displayPhotoError();
                });
            }

            @Override
            public void onTransferFailed() {
                super.onTransferFailed();
                dismissLoading();
                displayPhotoError();
            }

            @Override
            public void onError(int id, Exception ex) {
                super.onError(id, ex);
                dismissLoading();
                displayPhotoError();
            }
        };
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
                    requireActivity().onBackPressed();
                } else {
                    displayError();
                }
            }
        };
    }

    private void setupObjects() {
        userServices = new UserServices();
        mediaSelector = new MediaSelector(getContext());
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
        changePhoto.setOnClickListener(onPhotoClickListener);
        email.setEnabled(false);
        view.findViewById(R.id.passwordLayout).setVisibility(View.GONE);
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
        return Math.round(dps*density);
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

    public void onUpdateUser() {
        if (!validFieldsForCustomUser()) {
            return;
        }
        setLoadingMessage(getString(R.string.load_message_save_user));

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
        userServices.editUser(user, (firebaseError, firebase) ->
                firebaseCompletionListener.onComplete(firebaseError, firebase)
        );
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

    private View.OnClickListener onPhotoClickListener = view -> {
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.message_question_profile_picture)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaSelector.selectImage(EditUserFragment.this);
                    }
                })
                .show();
    };

    private MediaSelector.OnLoadLocalMediaListener onLoadLocalMediaListener = new MediaSelector.OnLoadLocalMediaListener() {
        @Override
        public void onLoadLocalImage(Uri uri) {
            LocalMedia localMedia = new LocalMedia(uri);
            localMedia.setType(Media.Type.Picture);
            transferMedia(getContext(), localMedia);
        }

        @Override
        public void onLoadLocalVideo(Uri uri) { }

        @Override
        public void onLoadFile(Uri uri) { }

        @Override
        public void onLoadAudio(Uri uri, int duration) { }

        private void transferMedia(Context context, LocalMedia localMedia) {
            if (firebaseImageTransferListenerAdapter == null)
                return;

            firebaseImageTransferListenerAdapter.onStart();
            try {
                TransferManager transferManager = new TransferManager(context);
                transferManager.transferMedia(localMedia, "user", new TransferListenerAdapter(context, localMedia) {
                    @Override
                    public void onTransferFinished(Media media) {
                        super.onTransferFinished(media);
                        firebaseImageTransferListenerAdapter.onTransferFinished(media);
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        super.onError(id, ex);
                        firebaseImageTransferListenerAdapter.onError(id, ex);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                firebaseImageTransferListenerAdapter.onTransferFailed();
            }
        }
    };

    private void displayPhotoError() {
        Toast.makeText(getContext(), R.string.error_image_upload, Toast.LENGTH_SHORT).show();
    }

    private void displayError() {
        Toast.makeText(getContext(), R.string.error_update_user, Toast.LENGTH_SHORT).show();
    }

    private boolean validFieldsForCustomUser() {
        return isUsernameValid() && isBirthdayValid() && isStateValid() && isSpinnerValid(gender);
    }

}