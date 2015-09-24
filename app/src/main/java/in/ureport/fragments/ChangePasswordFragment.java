package in.ureport.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import br.com.ilhasoft.support.tool.EditTextValidator;
import in.ureport.R;
import in.ureport.managers.FirebaseManager;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 11/09/15.
 */
public class ChangePasswordFragment extends Fragment {

    private static final String EXTRA_USER = "user";

    private EditTextValidator validator;

    private UserSettingsFragment.UserSettingsListener userSettingsListener;

    private EditText oldPassword;
    private EditText newPassword;

    private User user;

    private ProgressDialog progressDialog;

    public static ChangePasswordFragment newInstance(User user) {
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        changePasswordFragment.setArguments(args);
        return changePasswordFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null && args.containsKey(EXTRA_USER)) {
            user = args.getParcelable(EXTRA_USER);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof UserSettingsFragment.UserSettingsListener) {
            userSettingsListener = (UserSettingsFragment.UserSettingsListener)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObjects();
        setupView(view);
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

    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validateFields()) {
                progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.load_message_wait), true, false);
                FirebaseManager.changePassword(user, oldPassword.getText().toString()
                        , newPassword.getText().toString(), onPasswordChanged);
            }
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

    private Firebase.ResultHandler onPasswordChanged = new Firebase.ResultHandler() {
        @Override
        public void onSuccess() {
            progressDialog.dismiss();
            if (userSettingsListener != null) {
                userSettingsListener.onEditFinished();
            }
        }

        @Override
        public void onError(FirebaseError firebaseError) {
            progressDialog.dismiss();
            switch(firebaseError.getCode()) {
                case FirebaseError.INVALID_PASSWORD:
                    Toast.makeText(getActivity(), R.string.error_change_password, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getActivity(), R.string.error_no_internet, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
