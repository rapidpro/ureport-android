package in.ureport.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import in.ureport.R;
import in.ureport.activities.AboutActivity;
import in.ureport.activities.HomeActivity;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.network.UserServices;

public class SettingsFragment extends ProgressFragment {

    public static final String TAG = "GeneralSettingsFragment";
    private static final String URL_PRIVACY_POLICY = "https://ureport.in/about/";

    private User user;
    private boolean isAvailableOnChat;
    private UserServices userServices;

    private TextView chatAvailability;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        setupObjects();
        loadUser();
    }

    private void setupView(View view) {
        final TextView selectedCountry = view.findViewById(R.id.selectedCountry);
        selectedCountry.setText(CountryProgramManager.getCurrentCountryProgram().getName());

        final View selectCountry = view.findViewById(R.id.selectCountry);
        selectCountry.setOnClickListener(onSelectCountryClickListener);

        final View selectChatAvailability = view.findViewById(R.id.selectChatAvailability);
        selectChatAvailability.setOnClickListener(onSelectChatAvailabilityClickListener);
        chatAvailability = view.findViewById(R.id.chatAvailability);

        final View about = view.findViewById(R.id.about);
        about.setOnClickListener(onAboutClickListener);

        final View termsOfUse = view.findViewById(R.id.termsOfUse);

        final View privacyPolicy = view.findViewById(R.id.privacyPolicy);
        privacyPolicy.setOnClickListener(onPrivacyPolicyClickListener);

        final View logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(onLogoutClickListener);
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
                if (user != null) {
                    setUserChatAvailability(user.getPublicProfile());
                }
            }
        });
    }

    private void setUserChatAvailability(boolean available) {
        this.isAvailableOnChat = available;
        chatAvailability.setText(available ? R.string.label_available: R.string.label_unavailable);
    }

    private void restartHomeActivity() {
        final Activity activity = requireActivity();
        final Intent intent = new Intent(activity, HomeActivity.class);
        startActivity(intent);
        activity.finish();
    }

    private void displayMessage(int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener onSelectCountryClickListener = view -> {
        final List<CountryProgram> countryPrograms = CountryProgramManager.getAvailableCountryPrograms();
        final int size = countryPrograms.size();
        final String[] names = new String[size];

        for (int i=0; i<size; i++) {
            names[i] = countryPrograms.get(i).getName();
        }
        new AlertDialog.Builder(requireContext())
                .setItems(names, (dialog, which) -> {
                    CountryProgramManager.switchCountryProgram(countryPrograms.get(which));
                    restartHomeActivity();
                })
                .show();
    };

    private View.OnClickListener onSelectChatAvailabilityClickListener = view -> {
        final Context context = requireContext();
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_chat_availability, null);
        final RadioButton available = dialogView.findViewById(R.id.available);
        final RadioButton unavailable = dialogView.findViewById(R.id.unavailable);

        final DatabaseReference.CompletionListener onChangePublicProfileListener = (error, reference) -> {
            if (error == null) {
                displayMessage(R.string.message_success_user_update);
                setUserChatAvailability(isAvailableOnChat);
            } else {
                displayMessage(R.string.error_update_user);
            }
        };

        if (isAvailableOnChat) {
            available.setChecked(true);
        } else {
            unavailable.setChecked(true);
        }
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .show();
        available.setOnClickListener(radioView -> {
            isAvailableOnChat = true;
            userServices.changePublicProfile(user, true, onChangePublicProfileListener);
            dialog.dismiss();
        });
        unavailable.setOnClickListener(radioView -> {
            isAvailableOnChat = false;
            userServices.changePublicProfile(user, false, onChangePublicProfileListener);
            dialog.dismiss();
        });
    };

    private View.OnClickListener onAboutClickListener = view -> {
        final Intent intent = new Intent(requireContext(), AboutActivity.class);
        startActivity(intent);
    };

    private View.OnClickListener onPrivacyPolicyClickListener = view -> {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_PRIVACY_POLICY));
        startActivity(intent);
    };

    private View.OnClickListener onLogoutClickListener = view -> {
        final Activity activity = requireActivity();
        new AlertDialog.Builder(activity)
                .setMessage(R.string.question_logout)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    UserManager.logout(activity);
                    UserManager.startLoginFlow(activity);
                    activity.finish();
                })
                .setNegativeButton(R.string.cancel_dialog_button, (dialog, which) -> dialog.dismiss())
                .show();
    };

}
