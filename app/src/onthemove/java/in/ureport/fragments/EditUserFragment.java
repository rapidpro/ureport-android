package in.ureport.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import in.ureport.R;
import in.ureport.models.User;
import in.ureport.models.holders.UserGender;
import in.ureport.network.UserServices;
import in.ureport.tasks.SaveContactTask;
import io.rapidpro.sdk.core.models.base.ContactBase;

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
        email.setVisibility(View.GONE);

        confirm.setOnClickListener(onConfirmClickListener);
    }

    private void setupObjects() {
        userServices = new UserServices();
    }

    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validFieldsForCustomUser()) {
                user.setNickname(username.getText().toString());
                user.setBirthday(getBirthdayDate().getTime());

                UserGender gender = (UserGender) EditUserFragment.this.gender.getSelectedItem();
                user.setGenderAsEnum(gender.getGender());

                progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.load_message_wait), true, false);
                Log.i(TAG, "onClick editUser ");
                userServices.editUser(user, onUserUpdatedListener);
            }
        }
    };

    private DatabaseReference.CompletionListener onUserUpdatedListener = new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(final DatabaseError error, DatabaseReference reference) {
            Log.i(TAG, "onComplete editUser");
            progressDialog.dismiss();
            if (error == null) {
                updateContactToRapidpro();
            } else {
                displayError();
            }
        }

        private void updateContactToRapidpro() {
            SaveContactTask saveContactTask = new SaveContactTask(requireContext(), user, false) {
                @Override
                protected void onPostExecute(ContactBase contact) {
                    super.onPostExecute(contact);
                    if(contact != null) {
                        userSettingsListener.onEditFinished();
                    } else {
                        displayError();
                    }
                }
            };
            saveContactTask.execute();
        }
    };

    private void displayError() {
        Toast.makeText(getActivity(), R.string.error_update_user, Toast.LENGTH_SHORT).show();
    }

    private boolean validFieldsForCustomUser() {
        return isUsernameValid() && isBirthdayValid() && isSpinnerValid(gender);
    }
}
