package in.ureport.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.ilhasoft.support.tool.EditTextValidator;
import br.com.ilhasoft.support.widget.DatePickerFragment;
import in.ureport.R;
import in.ureport.loader.CountryListLoader;
import in.ureport.loader.LocationInfoLoader;
import in.ureport.models.User;
import in.ureport.models.geonames.Location;
import in.ureport.models.holders.LocationInfo;
import in.ureport.models.holders.UserGender;
import in.ureport.models.holders.UserLocale;

/**
 * Created by johncordeiro on 10/09/15.
 */
public abstract class UserInfoBaseFragment extends Fragment implements LoaderManager.LoaderCallbacks, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "UserInfoBaseFragment";

    protected static final String EXTRA_USER = "user";
    private static final String EXTRA_LOCALE_LOADER = "locale";

    public static final int FIELDS_MINIMUM_SIZE = 5;

    private static final int LOAD_COUNTRY_LIST_ID = 0;
    private static final int LOAD_STATES_ID = 1;

    protected User user;
    protected User.Type userType = User.Type.ureport;

    private EditTextValidator validator;
    private DateFormat birthdayFormatter;

    protected Toolbar toolbar;
    protected EditText username;
    protected EditText email;
    protected EditText password;
    protected EditText birthday;
    protected Spinner country;
    protected Spinner state;
    protected Spinner district;
    protected Spinner gender;
    protected Button confirm;

    protected boolean containsDistrict = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserFromArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
        setupUserIfNeeded();
    }

    private void setupUserIfNeeded() {
        if(user != null) {
            userType = user.getType();

            setEditTextValue(username, user.getNickname());
            setEditTextValue(email, user.getEmail());
            setEditTextDate(birthday, user.getBirthday());
            setPasswordVisibility();
            setUserGenderValue();
        }
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

    private void setupObjects() {
        validator = new EditTextValidator();
        birthdayFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
    }

    private void setupView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
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
        district = (Spinner) view.findViewById(R.id.district);
        confirm = (Button) view.findViewById(R.id.confirm);
    }

    private void getUserFromArguments() {
        Bundle args = getArguments();
        if(args != null && args.containsKey(EXTRA_USER)) {
            user = args.getParcelable(EXTRA_USER);
        }
    }

    protected void setEditTextValue(EditText editText, String value) {
        if(value != null && value.length() > 0) {
            editText.setText(value);
        }
    }

    protected void setEditTextDate(EditText editText, Date date) {
        if(date != null) {
            editText.setText(birthdayFormatter.format(date));
        }
    }

    private void loadCountryList() {
        getLoaderManager().initLoader(LOAD_COUNTRY_LIST_ID, null, this).forceLoad();
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

    protected boolean isFieldsValid() {
        boolean validTextFields = isUsernameValid();
        boolean validEmail = isEmailValid();
        boolean validBirthday = isBirthdayValid();

        return validTextFields && validEmail && validBirthday && isStateValid() && isDistrictValid() &&
                isSpinnerValid(country) && isSpinnerValid(gender) && isSpinnerValid(state) &&
                validatePasswordIfNeeded();
    }

    protected boolean isBirthdayValid() {
        return validator.validateEmpty(birthday, getString(R.string.error_required_field));
    }

    protected boolean isUsernameValid() {
        String messageNameValidation = getString(R.string.error_minimum_size
                , FIELDS_MINIMUM_SIZE);

        return validator.validateSizeMulti(FIELDS_MINIMUM_SIZE
                , messageNameValidation, username);
    }

    protected boolean isEmailValid() {
        return validator.validateEmail(email, getString(R.string.error_valid_email));
    }

    protected boolean isStateValid() {
        boolean valid = state.getSelectedItem() != null;
        if(!valid) Toast.makeText(getActivity(), R.string.error_choose_state, Toast.LENGTH_LONG).show();
        return valid;
    }

    protected boolean isDistrictValid() {
        if(!containsDistrict) return true;

        boolean valid = district.getSelectedItem() != null;
        if(!valid) Toast.makeText(getActivity(), R.string.error_choose_district, Toast.LENGTH_LONG).show();
        return valid;
    }

    private boolean validatePasswordIfNeeded() {
        String messageNameValidation = getString(R.string.error_minimum_size
                , FIELDS_MINIMUM_SIZE);
        return (userType == User.Type.ureport && validator.validateSize(password, FIELDS_MINIMUM_SIZE, messageNameValidation)
                || userType != User.Type.ureport);
    }

    protected boolean isSpinnerValid(Spinner spinner) {
        return spinner.getSelectedItemPosition() != Spinner.INVALID_POSITION;
    }

    protected UserLocale getUserLocale() {
        return (UserLocale) country.getAdapter().getItem(country.getSelectedItemPosition());
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOAD_COUNTRY_LIST_ID:
                return new CountryListLoader(getActivity());
            case LOAD_STATES_ID:
                Locale locale = (Locale) args.getSerializable(EXTRA_LOCALE_LOADER);
                return new LocationInfoLoader(getActivity(), locale);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOAD_COUNTRY_LIST_ID:
                updateUserLocale((List<UserLocale>)data);
                break;
            case LOAD_STATES_ID:
                LocationInfo locationInfo = (LocationInfo) data;
                updateSpinnerLocation(state, locationInfo.getStates());
                updateDistrictSpinner(locationInfo);
        }
    }

    private void updateDistrictSpinner(LocationInfo locationInfo) {
        containsDistrict = locationInfo.getDistricts() != null && !locationInfo.getDistricts().isEmpty();
        if(containsDistrict) {
            district.setVisibility(View.VISIBLE);
            updateSpinnerLocation(district, locationInfo.getDistricts());
        } else {
            district.setVisibility(View.GONE);
        }
    }

    private void updateSpinnerLocation(Spinner spinner, List<Location> locations) {
        if(locations != null) {
            ArrayAdapter<Location> adapter = new ArrayAdapter<>(getActivity(), R.layout.view_spinner_dropdown, locations);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setEnabled(true);
            spinner.setAdapter(adapter);

            onStatesLoaded(locations);
        } else {
            resetLocationSpinner(spinner, R.array.spinner_error_loading_states);
            Toast.makeText(getActivity(), R.string.error_no_internet, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}

    private void updateUserLocale(List<UserLocale> data) {
        ArrayAdapter<UserLocale> localeAdapter = new ArrayAdapter<>(getActivity(), R.layout.view_spinner_dropdown, data);
        localeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        country.setAdapter(localeAdapter);
        onCountriesLoaded(data);
    }

    @Nullable
    protected Date getBirthdayDate() {
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
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        birthday.setText(birthdayFormatter.format(calendar.getTime()));
    }

    private AdapterView.OnItemSelectedListener onCountrySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            UserLocale userLocale = (UserLocale) country.getSelectedItem();
            resetLocationSpinner(state, R.array.spinner_loading);
            resetLocationSpinner(district, R.array.spinner_loading);
            loadStatesForUserLocale(userLocale);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            resetLocationSpinner(state, R.array.spinner_loading);
            resetLocationSpinner(district, R.array.spinner_loading);
        }
    };

    private void resetLocationSpinner(Spinner spinner, @ArrayRes int loadingArrayMessage) {
        ArrayAdapter<String> loadingStatesAdapter = createArrayAdapterForStringArray(loadingArrayMessage);

        spinner.setAdapter(loadingStatesAdapter);
        spinner.setEnabled(false);
    }

    @NonNull
    private ArrayAdapter<String> createArrayAdapterForStringArray(@ArrayRes int stringArray) {
        ArrayAdapter<String> loadingStatesAdapter = new ArrayAdapter<>(getActivity()
                , R.layout.view_spinner_dropdown, getResources().getStringArray(stringArray));
        loadingStatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return loadingStatesAdapter;
    }

    private void loadStatesForUserLocale(UserLocale userLocale) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_LOCALE_LOADER, userLocale.getLocale());

        getLoaderManager().restartLoader(LOAD_STATES_ID, bundle, this).forceLoad();
    }

    private View.OnClickListener onBirthdayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setOnDateSetListener(UserInfoBaseFragment.this);
            datePickerFragment.show(getFragmentManager(), "datePickerFragment");
        }
    };

    public abstract void onCountriesLoaded(List<UserLocale> data);

    public abstract void onStatesLoaded(List<Location> locations);

}
