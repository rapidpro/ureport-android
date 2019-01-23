package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.ilhasoft.support.tool.EditTextValidator;
import in.ureport.R;

/**
 * Created by johncordeiro on 11/09/15.
 */
public class ChangePasswordFragment extends ProgressFragment {

    private EditTextValidator validator;

    private UserSettingsFragment.UserSettingsListener userSettingsListener;

    private EditText oldPassword;
    private EditText newPassword;

    private FirebaseAuth firebaseAuth;
    private static OnCompleteListener<Void> userReauthenticationListener;
    private static OnCompleteListener<Void> passwordChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserSettingsFragment.UserSettingsListener) {
            userSettingsListener = (UserSettingsFragment.UserSettingsListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObjects();
        setupView(view);
        setupContextDependencies();
        setLoadingMessage(getString(R.string.load_message_wait));
    }

    private void setupView(View view) {
        oldPassword = (EditText) view.findViewById(R.id.oldPassword);
        newPassword = (EditText) view.findViewById(R.id.newPassword);

        Button confirm = (Button) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(onConfirmClickListener);
    }

    private void setupObjects() {
        validator = new EditTextValidator();
    }

    private void setupContextDependencies() {
        userReauthenticationListener = task -> {
            dismissLoading();
            if (task.getException() == null) {
                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) return;

                showLoading();
                currentUser.updatePassword(newPassword.getText().toString())
                        .addOnCompleteListener(passwordChangeTask ->
                                passwordChangeListener.onComplete(passwordChangeTask));
            } else {
                Toast.makeText(getContext(), R.string.error_change_password, Toast.LENGTH_SHORT).show();
            }
        };
        passwordChangeListener = task -> {
            dismissLoading();
            if (task.getException() == null) {
                if (userSettingsListener != null) {
                    userSettingsListener.onEditFinished();
                }
            } else {
                Toast.makeText(getContext(), R.string.error_update_user, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void updateUserPassword() {
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        showLoading();
        final String userEmail = currentUser.getEmail();
        final AuthCredential credential = EmailAuthProvider
                .getCredential(userEmail == null ? "" : userEmail, oldPassword.getText().toString());

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> userReauthenticationListener.onComplete(task));
    }

    private View.OnClickListener onConfirmClickListener = view -> {
        if (validateFields()) {
            updateUserPassword();
        }
    };

    private boolean validateFields() {
        return validateSize(oldPassword) && validateSize(newPassword);
    }

    @NonNull
    private Boolean validateSize(EditText editText) {
        String messageNameValidation = getString(R.string.error_minimum_size, UserInfoBaseFragment.FIELDS_MINIMUM_SIZE);
        return validator.validateSize(editText, UserInfoBaseFragment.FIELDS_MINIMUM_SIZE, messageNameValidation);
    }

}
