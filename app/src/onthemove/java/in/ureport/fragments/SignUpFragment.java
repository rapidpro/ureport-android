package in.ureport.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import in.ureport.R;
import in.ureport.helpers.AnalyticsHelper;
import in.ureport.helpers.ToolbarDesigner;
import in.ureport.managers.FirebaseManager;
import in.ureport.models.User;
import in.ureport.models.holders.Login;
import in.ureport.models.holders.UserGender;
import in.ureport.network.UserServices;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class SignUpFragment extends UserInfoBaseFragment {

    private static final String TAG = "SignUpFragment";

    private LoginFragment.LoginListener loginListener;

    private ProgressDialog progressDialog;

    public static SignUpFragment newInstance(User user) {
        SignUpFragment signUpFragment = new SignUpFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        signUpFragment.setArguments(args);
        return signUpFragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if(activity instanceof LoginFragment.LoginListener) {
            loginListener = (LoginFragment.LoginListener)activity;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
    }

    protected void setupView() {
        confirm.setOnClickListener(onConfirmClickListener);

        toolbar.setVisibility(View.VISIBLE);
        ToolbarDesigner toolbarDesigner = new ToolbarDesigner();
        toolbarDesigner.setupFragmentDefaultToolbar(toolbar, R.string.label_data_confirmation, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private User createUser() {
        User user = new User();
        user.setType(userType);
        user.setNickname(username.getText().toString());
        user.setEmail(email.getText().toString());
        user.setBirthday(getBirthdayDate());

        if(userType != User.Type.ureport) {
            user.setKey(this.user.getKey());
            user.setPicture(this.user.getPicture());
        }

        user.setCountry("OT");
        user.setCountryProgram("OTM");

        UserGender userGender = (UserGender)gender.getAdapter().getItem(gender.getSelectedItemPosition());
        user.setGenderAsEnum(userGender.getGender());
        return user;
    }

    private View.OnClickListener onConfirmClickListener = view -> {
        if(isFieldsValid()) {
            showDialog();

            final User user1 = createUser();
            Login login = getLoginData(user1);

            switch (user1.getType()) {
                case ureport:
                    createUserAndAuthenticate(login, user1);
                    break;
                default:
                    storeUserAndFinish(user1);
            }
        }
    };

    private void createUserAndAuthenticate(final Login login, final User user) {
        FirebaseManager.getReference().createUser(login.getEmail(), login.getPassword(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                user.setKey(result.get("uid").toString());
                authenticateAndSaveUser(login, user);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                dismissDialog();
                Toast.makeText(getActivity(), R.string.error_email_already_exists, Toast.LENGTH_LONG).show();

                AnalyticsHelper.sendFirebaseError(firebaseError);
            }
        });
    }

    private void authenticateAndSaveUser(Login login, final User user) {
        FirebaseManager.getReference().authWithPassword(login.getEmail(), login.getPassword(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                dismissDialog();
                storeUserAndFinish(user);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                dismissDialog();
                Toast.makeText(getActivity(), R.string.error_valid_email, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void storeUserAndFinish(final User user) {
        showDialog();
        UserServices userServices = new UserServices();
        userServices.saveUser(user, (firebaseError, firebase) -> {
            dismissDialog();
            if (firebaseError != null)
                Toast.makeText(getActivity().getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            else
                loginListener.onUserReady(user, true);
        });
    }

    @NonNull
    private Login getLoginData(User user) {
        String email = user.getEmail();
        String password = SignUpFragment.this.password.getText().toString();

        return new Login(email, password);
    }

    private void showDialog() {
        progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.load_registering_user)
                , true, false);
    }

    private void dismissDialog() {
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
