package in.ureport.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.helpers.AnalyticsHelper;
import in.ureport.helpers.ToolbarDesigner;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseManager;
import in.ureport.models.User;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
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
        setupUserIfExists();
    }

    private void setupView() {
        confirm.setOnClickListener(onConfirmClickListener);

        toolbar.setVisibility(View.VISIBLE);
        ToolbarDesigner toolbarDesigner = new ToolbarDesigner();
        toolbarDesigner.setupFragmentDefaultToolbar(toolbar, R.string.label_data_confirmation, this);
    }

    @Override
    public void onCountriesLoaded(List<CountryInfo> data) {
        if(countryInfo == null) {
            selectCurrentUserLocale(data);
        }
    }

    @Override
    public void onStatesLoaded(List<Location> locations) {}

    private void setupUserIfExists() {
        if(user != null) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.light_cyan_highlight));
            setSignupStatusBarColor(R.color.dark_cyan_highlight);
            confirm.setBackgroundResource(R.drawable.shape_round_simple_blue_button);
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.light_yellow_highlight));
            setSignupStatusBarColor(R.color.dark_yellow_highlight);
        }
    }

    private void setSignupStatusBarColor(@ColorRes int color) {
        StatusBarDesigner statusBarDesigner = new StatusBarDesigner();
        statusBarDesigner.setStatusBarColorById(getActivity(), color);
    }

    private void selectCurrentUserLocale(List<CountryInfo> countries) {
        Locale locale = Locale.getDefault();
        CountryInfo countryInfo = new CountryInfo(locale.getISO3Country());
        int countryPosition = countries.indexOf(countryInfo);

        if(countryPosition >= 0) {
            country.setSelection(countryPosition);
        }
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

        Location state = (Location)this.state.getSelectedItem();
        user.setState(state.getName());

        if(containsDistrict) {
            Location district = (Location) this.district.getSelectedItem();
            user.setDistrict(district.getName());
        }

        if(userType != User.Type.ureport) {
            user.setKey(this.user.getKey());
            user.setPicture(this.user.getPicture());
        }

        CountryInfo countryInfo = getCountrySelected();
        String countryCode = countryInfo.getIsoAlpha3();
        user.setCountry(countryCode);
        user.setCountryProgram(CountryProgramManager.getCountryProgramForCode(countryCode).getCode());

        UserGender userGender = (UserGender)gender.getAdapter().getItem(gender.getSelectedItemPosition());
        user.setGender(userGender.getGender());
        return user;
    }

    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isFieldsValid()) {
                showDialog();

                final User user = createUser();
                Login login = getLoginData(user);

                switch (user.getType()) {
                    case ureport:
                        createUserAndAuthenticate(login, user);
                        break;
                    default:
                        storeUserAndFinish(user);
                }
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
        userServices.saveUser(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                dismissDialog();
                if (firebaseError != null)
                    Toast.makeText(getActivity().getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                else
                    loginListener.onUserReady(user, true);
            }
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
