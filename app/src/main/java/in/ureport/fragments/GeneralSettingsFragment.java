package in.ureport.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.FileNotFoundException;

import in.ureport.R;
import in.ureport.helpers.PermissionHelper;
import in.ureport.helpers.UserDataDoc;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.models.UserDataResponse;
import in.ureport.network.UserServices;

/**
 * Created by johncordeiro on 18/09/15.
 */
public class GeneralSettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = "GeneralSettingsFragment";

    private static final String PUBLIC_PROFILE_KEY = "pref_key_chat_available";
    private static final String DATA_EXPORT_KEY = "pref_key_export_data";
    private static final String ACCOUNT_CLOSE_KEY = "pref_key_close_account";
    private static final String CHAT_NOTIFICATIONS_KEY = "pref_key_chat_notifications";

    private static final int REQUEST_CODE_WRITE_PDF = 100;

    private UserServices userServices;
    private FirebaseFunctions firebaseFunctions;

    private SwitchPreferenceCompat publicProfilePreference;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFunctions = FirebaseFunctions.getInstance();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.general_settings_preferences, rootKey);

        setupView();
        setupObjects();
        loadUser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_PDF && PermissionHelper.allPermissionsGranted(grantResults)) {
            downloadUserData();
        }
    }

    private void setupView() {
        publicProfilePreference = (SwitchPreferenceCompat) getPreferenceManager().findPreference(PUBLIC_PROFILE_KEY);
        publicProfilePreference.setOnPreferenceChangeListener(onPublicProfilePreferenceChangeListener);
        Preference dataExportPreference = getPreferenceManager().findPreference(DATA_EXPORT_KEY);
        dataExportPreference.setOnPreferenceClickListener(dataExportClickListener);
        Preference accountClosePreference = getPreferenceManager().findPreference(ACCOUNT_CLOSE_KEY);
        accountClosePreference.setOnPreferenceClickListener(accountCloseClickListener);
    }

    private void setupObjects() {
        userServices = new UserServices();
    }

    private void loadUser() {
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                user = dataSnapshot.getValue(User.class);
                if (user != null) updateViewForUser(user);
            }
        });
    }

    private void updateViewForUser(User user) {
        publicProfilePreference.setChecked(user.getPublicProfile());
    }

    private void downloadUserData() {
        if (!PermissionHelper.isPermissionGranted(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PDF);
            return;
        }
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null,
                getString(R.string.load_message_wait), true, false);

        firebaseFunctions.getHttpsCallable("exportData")
                .call()
                .continueWith(task -> {
                    try {
                        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        final JsonElement json = gson.toJsonTree(task.getResult().getData());
                        return gson.fromJson(json, UserDataResponse.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.getResult() != null) {
                        generateUserDataPdf(task.getResult());
                    }
                });
    }

    private void generateUserDataPdf(final UserDataResponse response) {
        try {
            final File pdfFile = UserDataDoc.makeUserDataPdf(getResources(), response);
            final Uri pdfFileUri = FileProvider.getUriForFile(
                    requireContext(),
                    "in.ureport.UreportApplication.provider",
                    pdfFile
            );
            final Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setDataAndType(pdfFileUri, "application/pdf");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage(R.string.user_data_pdf_error);
        }
    }

    private void displayAccountClosureAlert() {
        final View confirmView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_account_closure, null);
        final TextView confirmWarning = confirmView.findViewById(R.id.confirmWarning);
        final EditText confirmInput = confirmView.findViewById(R.id.confirmInput);

        final String confirmText = getString(R.string.confirm).toUpperCase();
        confirmWarning.setText(getString(R.string.type_confirm, confirmText));

        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.are_you_sure)
                .setView(confirmView)
                .setPositiveButton(R.string.yes, (dialog, i) -> {
                    if (confirmInput.getText().toString().equals(confirmText)) {
                        deleteUserAccount();
                    } else {
                        displayMessage(R.string.error_type_confirm);
                    }
                })
                .setNegativeButton(R.string.no, (dialog, i) -> dialog.cancel())
                .show();
    }

    private void deleteUserAccount() {
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null,
                getString(R.string.load_message_wait), true, false);

        firebaseFunctions.getHttpsCallable("clearData")
                .call()
                .continueWith(Task::getResult)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.getResult() != null) {
                        displayMessage(R.string.account_deleted);
                        UserManager.logout(requireContext());
                        UserManager.startLoginFlow(requireContext());
                    }
                });
    }

    private Preference.OnPreferenceChangeListener onPublicProfilePreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object object) {
            publicProfilePreference.setChecked(!publicProfilePreference.isChecked());
            if (user != null) {
                userServices.changePublicProfile(user, publicProfilePreference.isChecked(), onSettingsSavedListener);
            } else {
                displayMessage(R.string.error_update_user);
            }
            return false;
        }
    };

    private DatabaseReference.CompletionListener onSettingsSavedListener = (error, reference) -> {
        if (error == null) {
            displayMessage(R.string.message_success_user_update);
        } else {
            displayMessage(R.string.error_update_user);
        }
    };

    private void displayMessage(int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private Preference.OnPreferenceClickListener dataExportClickListener = preference -> {
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.message_user_data_export)
                .setPositiveButton(R.string.yes, (dialog, i) -> downloadUserData())
                .setNegativeButton(R.string.no, (dialog, i) -> dialog.dismiss())
                .show();
        return true;
    };

    private Preference.OnPreferenceClickListener accountCloseClickListener = preference -> {
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.message_user_account_close)
                .setPositiveButton(R.string.yes, (dialog, i) -> displayAccountClosureAlert())
                .setNegativeButton(R.string.no, (dialog, i) -> dialog.dismiss())
                .show();
        return true;
    };


}
