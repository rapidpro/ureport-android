package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Locale;

import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.BuildConfig;
import in.ureport.R;
import in.ureport.helpers.AnalyticsHelper;
import in.ureport.helpers.ToolbarDesigner;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.User;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import in.ureport.models.holders.Login;
import in.ureport.models.holders.UserGender;
import in.ureport.models.ip.IpResponse;
import in.ureport.network.IpServices;
import in.ureport.network.UserServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class SignUpFragment extends UserInfoBaseFragment {

    private static final String TAG = "SignUpFragment";

    private LoginFragment.LoginListener loginListener;
    private FirebaseAuth firebaseAuth;

    private static UserSignupListener userSignupListener;
    private static UserSigninListener userSigninListener;
    private static UserSaveListener userSaveListener;

    public static SignUpFragment newInstance(User user) {
        SignUpFragment signUpFragment = new SignUpFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        signUpFragment.setArguments(args);
        return signUpFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof LoginFragment.LoginListener) {
            loginListener = (LoginFragment.LoginListener) activity;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupContextDependencies();
        setupView();
        setupUserIfExists();
        setLoadingMessage(getString(R.string.load_registering_user));
    }

    private void setupContextDependencies() {
        userSignupListener = new UserSignupListener() {
            @Override
            public void onSuccess(AuthResult result, User user, Login login) {
                user.setKey(result.getUser().getUid());
                authenticateAndSaveUser(login, user);
            }

            @Override
            public void onError(Exception e) {
                dismissLoading();
                Toast.makeText(getContext(), R.string.error_email_already_exists, Toast.LENGTH_LONG).show();
                AnalyticsHelper.sendException(e);
            }
        };
        userSigninListener = new UserSigninListener() {
            @Override
            public void onAuthenticated(User user) {
                dismissLoading();
                storeUserAndFinish(user);
            }

            @Override
            public void onError(Exception e) {
                dismissLoading();
                Toast.makeText(getContext(), R.string.error_valid_email, Toast.LENGTH_LONG).show();
            }
        };
        userSaveListener = ((error, user) -> {
            dismissLoading();
            if (error != null) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                loginListener.onUserReady(user, true);
            }
        });
    }

    protected void setupView() {
        confirm.setOnClickListener(onConfirmClickListener);

        toolbar.setVisibility(View.VISIBLE);
        ToolbarDesigner toolbarDesigner = new ToolbarDesigner();
        toolbarDesigner.setupFragmentDefaultToolbar(toolbar, R.string.label_data_confirmation, this);
    }

    @Override
    public void onCountriesLoaded(List<CountryInfo> data) {
        if (countryInfo == null) {
            selectCurrentUserLocale(data);
        }
    }

    @Override
    public void onStatesLoaded(List<Location> locations) {
    }

    private void setupUserIfExists() {
        if (BuildConfig.FLAVOR.equals("onthemove")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.primary_color));
            setSignupStatusBarColor(R.color.primary_color);
        } else {
            if (user != null) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.light_cyan_highlight));
                setSignupStatusBarColor(R.color.dark_cyan_highlight);
                confirm.setBackgroundResource(R.drawable.shape_round_simple_blue_button);
            } else {
                toolbar.setBackgroundColor(getResources().getColor(R.color.light_yellow_highlight));
                setSignupStatusBarColor(R.color.dark_yellow_highlight);
            }
        }
    }

    private void setSignupStatusBarColor(@ColorRes int color) {
        StatusBarDesigner statusBarDesigner = new StatusBarDesigner();
        statusBarDesigner.setStatusBarColorById(getActivity(), color);
    }

    private void selectCurrentUserLocale(List<CountryInfo> countries) {
        IpServices ipServices = new IpServices();
        ipServices.getIp().enqueue(new Callback<IpResponse>() {
            @Override
            public void onResponse(Call<IpResponse> call, Response<IpResponse> response) {
                final IpResponse ipResponse = response.body();
                if (ipResponse != null) {
                    CountryInfo countryInfo = new CountryInfo(ipResponse.getCountryCode());
                    setupCountryInfo(countryInfo, countries);
                }
            }

            @Override
            public void onFailure(Call<IpResponse> call, Throwable t) {
                Locale locale = Locale.getDefault();
                CountryInfo countryInfo = new CountryInfo(locale.getCountry());
                setupCountryInfo(countryInfo, countries);
            }
        });
    }

    private void setupCountryInfo(CountryInfo countryInfo, List<CountryInfo> countries) {
        int countryPosition = countries.indexOf(countryInfo);

        if (countryPosition >= 0) {
            country.setSelection(countryPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        user.setBirthday(getBirthdayDate().getTime());

        Location state = (Location) this.state.getSelectedItem();
        user.setState(state.getName());

        if (containsDistrict) {
            Location district = (Location) this.district.getSelectedItem();
            user.setDistrict(district.getName());
        }

        if (userType != User.Type.ureport) {
            user.setKey(this.user.getKey());
            user.setPicture(this.user.getPicture());
        }

        CountryInfo countryInfo = getCountrySelected();
        String countryCode = countryInfo.getIsoAlpha3();
        user.setCountry(countryCode);
        user.setCountryProgram(CountryProgramManager.getCountryProgramForCode(countryCode).getCode());

        UserGender userGender = (UserGender) gender.getAdapter().getItem(gender.getSelectedItemPosition());
        user.setGenderAsEnum(userGender.getGender());
        return user;
    }

    private View.OnClickListener onConfirmClickListener = view -> {
        if (isFieldsValid()) {
            showLoading();

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
    };

    private void createUserAndAuthenticate(final Login login, final User user) {
        firebaseAuth.createUserWithEmailAndPassword(login.getEmail(), login.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        userSignupListener.onSuccess(task.getResult(), user, login);
                    } else {
                        userSignupListener.onError(task.getException());
                    }
                });
    }

    private void authenticateAndSaveUser(final Login login, final User user) {
        firebaseAuth.signInWithEmailAndPassword(login.getEmail(), login.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userSigninListener.onAuthenticated(user);
                    } else {
                        userSigninListener.onError(task.getException());
                    }
                });
    }

    private void storeUserAndFinish(final User user) {
        showLoading();
        UserServices userServices = new UserServices();
        userServices.saveUser(user, (firebaseError, firebase) ->
                userSaveListener.onComplete(firebaseError, user));
    }

    @NonNull
    private Login getLoginData(User user) {
        String email = user.getEmail();
        String password = SignUpFragment.this.password.getText().toString();

        return new Login(email, password);
    }

    interface UserSignupListener {
        void onSuccess(AuthResult result, User user, Login login);
        void onError(Exception e);
    }

    interface UserSigninListener {
        void onAuthenticated(User user);
        void onError(Exception e);
    }

    interface UserSaveListener {
        void onComplete(DatabaseError error, User user);
    }

}