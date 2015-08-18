package in.ureport.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.ilhasoft.support.tool.EditTextValidator;
import br.com.ilhasoft.support.tool.StatusBarDesigner;
import br.com.ilhasoft.support.widget.DatePickerFragment;
import in.ureport.R;
import in.ureport.loader.CountryListLoader;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.ToolbarDesigner;
import in.ureport.models.User;
import in.ureport.models.geonames.State;
import in.ureport.models.holders.Login;
import in.ureport.models.holders.UserGender;
import in.ureport.models.holders.UserLocale;
import in.ureport.network.UserServices;
import in.ureport.tasks.LoadStatesTask;
import in.ureport.tasks.SaveContactTask;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class SignUpFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<UserLocale>>, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "SignUpFragment";

    private static final String EXTRA_USER = "user";

    private static final int FIELDS_MINIMUM_SIZE = 5;

    private EditText username;

    private EditText email;
    private EditText password;
    private EditText birthday;
    private Spinner country;
    private Spinner state;
    private Spinner gender;
    private EditTextValidator validator;
    private Toolbar toolbar;
    private Button confirm;

    private DateFormat birthdayFormatter;
    private User user;
    private User.Type userType = User.Type.ureport;
    private LoginFragment.LoginListener loginListener;

    private ProgressDialog progressDialog;

    public static SignUpFragment newInstance(User user) {
        SignUpFragment signUpFragment = new SignUpFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);

        signUpFragment.setArguments(args);
        return signUpFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        validator = new EditTextValidator();
        birthdayFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
        getUserFromArguments();

        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof LoginFragment.LoginListener) {
            loginListener = (LoginFragment.LoginListener)activity;
        }
    }

    private void getUserFromArguments() {
        Bundle args = getArguments();
        if(args != null && args.containsKey(EXTRA_USER)) {
            user = args.getParcelable(EXTRA_USER);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        setupUserIfExists();
    }

    private void setupView(View view) {
        username = (EditText) view.findViewById(R.id.username);
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);

        birthday = (EditText) view.findViewById(R.id.birthday);
        birthday.setOnClickListener(onBirthdayClickListener);

        gender = (Spinner) view.findViewById(R.id.gender);
        loadUserGenders();

        country = (Spinner) view.findViewById(R.id.country);
        country.setOnItemSelectedListener(onCountrySelectedListener);
        loadCountryList();

        state = (Spinner) view.findViewById(R.id.state);

        confirm = (Button) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(onConfirmClickListener);

        toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        ToolbarDesigner toolbarDesigner = new ToolbarDesigner();
        toolbarDesigner.setupFragmentDefaultToolbar(toolbar, R.string.label_data_confirmation, this);
    }

    private void setupUserIfExists() {
        if(user != null) {
            userType = user.getType();

            setEditTextValue(username, user.getNickname());
            setEditTextValue(email, user.getEmail());
            setEditTextDate(birthday, user.getBirthday());
            setPasswordVisibility();
            setUserGenderValue();

            toolbar.setBackgroundColor(getResources().getColor(R.color.confirm_info_primary_color));
            setSignupStatusBarColor(R.color.confirm_info_primary_color_dark);
            confirm.setBackgroundResource(R.drawable.shape_round_simple_blue_button);
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.signup_primary_color));
            setSignupStatusBarColor(R.color.signup_primary_color_dark);
        }
    }

    private void setSignupStatusBarColor(@ColorRes int color) {
        StatusBarDesigner statusBarDesigner = new StatusBarDesigner();
        statusBarDesigner.setStatusBarColorById(getActivity(), color);
    }

    private void setPasswordVisibility() {
        if(userType != User.Type.ureport) {
            password.setVisibility(View.GONE);
        }
    }

    private void setUserGenderValue() {
        UserGender userGender = new UserGender(getActivity(), user.getGender());
        ArrayAdapter<UserGender> adapter = ((ArrayAdapter<UserGender>) gender.getAdapter());
        int position = adapter.getPosition(userGender);

        gender.setSelection(position);
    }

    private void setEditTextValue(EditText editText, String value) {
        if(value != null && value.length() > 0) {
            editText.setText(value);
        }
    }

    private void setEditTextDate(EditText editText, Date date) {
        if(date != null) {
            editText.setText(birthdayFormatter.format(date));
        }
    }

    private void loadUserGenders() {
        List<UserGender> userGenders = getUserGenders();

        ArrayAdapter<UserGender> genderAdapter = new ArrayAdapter<>(getActivity(), R.layout.view_spinner_dropdown, userGenders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);
    }

    @NonNull
    private List<UserGender> getUserGenders() {
        List<UserGender> userGenders = new ArrayList<>();
        User.Gender[] genders = User.Gender.values();
        for (User.Gender gender : genders) {
            UserGender userGender = new UserGender(getActivity(), gender);
            userGenders.add(userGender);
        }
        return userGenders;
    }

    private void loadCountryList() {
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private void selectCurrentUserLocale(List<UserLocale> userLocales) {
        Locale locale = Locale.getDefault();
        UserLocale userLocale = new UserLocale(locale);
        int userLocalePosition = userLocales.indexOf(userLocale);

        if(userLocalePosition >= 0) {
            country.setSelection(userLocalePosition);
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

    private boolean isFieldsValid() {
        String messageNameValidation = getString(R.string.error_minimum_size
                , FIELDS_MINIMUM_SIZE);

        boolean validTextFields = validator.validateSizeMulti(FIELDS_MINIMUM_SIZE
                , messageNameValidation, username);
        boolean validEmail = validator.validateEmail(email, getString(R.string.error_valid_email));
        boolean validBirthday = validator.validateEmpty(birthday, getString(R.string.error_required_field));

        return validTextFields && validEmail && validBirthday && isStateValid() &&
                isSpinnerValid(country) && isSpinnerValid(gender) && isSpinnerValid(state) &&
                validatePasswordIfNeeded(messageNameValidation);
    }

    private boolean isStateValid() {
        boolean validState = state.getSelectedItem() != null;
        if(!validState) Toast.makeText(getActivity(), "Choose the state of the country!", Toast.LENGTH_LONG).show();
        return validState;
    }

    private boolean validatePasswordIfNeeded(String messageNameValidation) {
        return (userType == User.Type.ureport && validator.validateSize(password, FIELDS_MINIMUM_SIZE, messageNameValidation)
                || userType != User.Type.ureport);
    }

    private boolean isSpinnerValid(Spinner spinner) {
        return spinner.getSelectedItemPosition() != Spinner.INVALID_POSITION;
    }

    @NonNull
    private User createUser() {
        User user = new User();
        user.setType(userType);
        user.setNickname(username.getText().toString());
        user.setEmail(email.getText().toString());
        user.setBirthday(getBirthdayDate());

        State state = (State)this.state.getSelectedItem();
        user.setState(state.getToponymName());

        if(userType != User.Type.ureport) {
            user.setKey(this.user.getKey());
            user.setPicture(this.user.getPicture());
        }

        UserLocale userLocale = (UserLocale) country.getAdapter().getItem(country.getSelectedItemPosition());
        String displayCountry = userLocale.getLocale().getISO3Country();
        user.setCountry(displayCountry);

        UserGender userGender = (UserGender)gender.getAdapter().getItem(gender.getSelectedItemPosition());
        user.setGender(userGender.getGender());
        return user;
    }

    @Nullable
    private Date getBirthdayDate() {
        String birthdayAsText = birthday.getText().toString();
        Date birthdayAsDate = null;
        try {
            birthdayAsDate = birthdayFormatter.parse(birthdayAsText);
        } catch (ParseException exception) {
            Log.e(TAG, "getBirthdayDate ", exception);
        }
        return birthdayAsDate;
    }

    @Override
    public Loader<List<UserLocale>> onCreateLoader(int id, Bundle args) {
        return new CountryListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<UserLocale>> loader, List<UserLocale> data) {
        updateUserLocale(data);
    }

    private void updateUserLocale(List<UserLocale> data) {
        ArrayAdapter<UserLocale> localeAdapter = new ArrayAdapter<>(getActivity(), R.layout.view_spinner_dropdown, data);
        localeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        country.setAdapter(localeAdapter);
        selectCurrentUserLocale(data);
    }

    @Override
    public void onLoaderReset(Loader<List<UserLocale>> loader) {}

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        birthday.setText(birthdayFormatter.format(calendar.getTime()));
    }

    private View.OnClickListener onBirthdayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setOnDateSetListener(SignUpFragment.this);
            datePickerFragment.show(getFragmentManager(), "datePickerFragment");
        }
    };

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
                authenticateUserAndStore(login, user);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                dismissDialog();
                Toast.makeText(getActivity(), R.string.error_email_already_exists, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void authenticateUserAndStore(Login login, final User user) {
        FirebaseManager.getReference().authWithPassword(login.getEmail(), login.getPassword(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
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
        UserServices userServices = new UserServices();
        userServices.saveUser(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                dismissDialog();
                if (firebaseError != null)
                    Toast.makeText(getActivity().getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                else
                    finishRegistration(user);
            }
        });
    }

    private void finishRegistration(final User user) {
        SaveContactTask saveContactTask = new SaveContactTask(getActivity()) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loginListener.onUserReady(user);
            }
        };
        saveContactTask.execute(user);
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

    private AdapterView.OnItemSelectedListener onCountrySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            UserLocale userLocale = (UserLocale)country.getSelectedItem();
            resetStateSpinner();
            loadStatesForUserLocale(userLocale);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            resetStateSpinner();
        }
    };

    private void resetStateSpinner() {
        state.setAdapter(null);
        state.setEnabled(false);
    }

    private void loadStatesForUserLocale(UserLocale userLocale) {
        LoadStatesTask loadStatesTask = new LoadStatesTask() {
            @Override
            protected void onPostExecute(List<State> states) {
                super.onPostExecute(states);

                if(states != null) {
                    ArrayAdapter<State> statesAdapter = new ArrayAdapter<>(getActivity(), R.layout.view_spinner_dropdown, states);
                    statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    state.setEnabled(true);
                    state.setAdapter(statesAdapter);
                } else {
                    Toast.makeText(getActivity(), R.string.error_no_internet, Toast.LENGTH_LONG).show();
                }
            }
        };
        loadStatesTask.execute(userLocale.getLocale());
    }
}
