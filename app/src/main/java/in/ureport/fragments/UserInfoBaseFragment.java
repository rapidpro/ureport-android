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

import br.com.ilhasoft.support.tool.EditTextValidator;
import br.com.ilhasoft.support.widget.DatePickerFragment;
import in.ureport.R;
import in.ureport.loader.CountryListLoader;
import in.ureport.loader.LocationInfoLoader;
import in.ureport.models.User;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.geonames.Location;
import in.ureport.models.holders.LocationInfo;
import in.ureport.models.holders.UserGender;

/**
 * Created by johncordeiro on 10/09/15.
 */
public abstract class UserInfoBaseFragment extends Fragment implements LoaderManager.LoaderCallbacks, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "UserInfoBaseFragment";

    protected static final String EXTRA_USER = "user";
    private static final String EXTRA_COUNTRY_INFO = "countryInfo";

    public static final int FIELDS_MINIMUM_SIZE = 5;

    private static final int LOAD_COUNTRY_LIST_ID = 3000;
    private static final int LOAD_STATES_ID = 3001;

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
    private List<Location> districts;

    protected CountryInfo countryInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCountryInfoFromState(savedInstanceState);
        getUserFromArguments();
        loadCountryList();
    }

    private void getCountryInfoFromState(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_COUNTRY_INFO)) {
            countryInfo = savedInstanceState.getParcelable(EXTRA_COUNTRY_INFO);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_COUNTRY_INFO, countryInfo);
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
        resetLocationSpinner(country, R.array.spinner_loading);

        state = (Spinner) view.findViewById(R.id.state);
        state.setOnItemSelectedListener(onStateSelectedListener);
        resetLocationSpinner(state, R.array.spinner_loading);

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
        getLoaderManager().initLoader(LOAD_COUNTRY_LIST_ID, null, this);
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
        boolean valid = state.getSelectedItem() != null && state.getSelectedItem() instanceof Location;
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

    protected CountryInfo getCountrySelected() {
        return (CountryInfo) country.getAdapter().getItem(country.getSelectedItemPosition());
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() called with: " + "id = [" + id + "], args = [" + args + "]");
        switch(id) {
            case LOAD_COUNTRY_LIST_ID:
                return new CountryListLoader(getActivity());
            case LOAD_STATES_ID:
                CountryInfo countryInfo = args.getParcelable(EXTRA_COUNTRY_INFO);
                return new LocationInfoLoader(getActivity(), countryInfo);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Log.d(TAG, "onLoadFinished() called with: " + "loader = [" + loader + "], data = [" + data + "]");
        switch (loader.getId()) {
            case LOAD_COUNTRY_LIST_ID:
                updateCountries((List<CountryInfo>)data);
                break;
            case LOAD_STATES_ID:
                LocationInfo locationInfo = (LocationInfo) data;
                updateStateSpinner(locationInfo);

                districts = locationInfo.getDistricts();
                updateDistrictSpinnerForState((Location) state.getSelectedItem());
        }
    }

    private void updateStateSpinner(LocationInfo locationInfo) {
        updateSpinnerLocation(state, locationInfo.getStates());
        onStatesLoaded(locationInfo.getStates());
    }

    private void updateDistrictSpinnerForState(Location state) {
        List<Location> districtsFromState = getDistrictsFromState(state);
        containsDistrict = !districtsFromState.isEmpty();

        if(containsDistrict) {
            district.setVisibility(View.VISIBLE);
            updateSpinnerLocation(district, districtsFromState);
        } else {
            district.setVisibility(View.GONE);
        }
    }

    @NonNull
    private List<Location> getDistrictsFromState(Location stateSelectedItem) {
        List<Location> stateDistricts = new ArrayList<>();
        if(districts != null) {
            for (Location location : districts) {
                if(location.getParent() != null && location.getParent().equals(stateSelectedItem.getBoundary())) {
                    stateDistricts.add(location);
                }
            }
        }
        return stateDistricts;
    }

    private void updateSpinnerLocation(Spinner spinner, List<Location> locations) {
        if(locations != null) {
            ArrayAdapter<Location> adapter = new ArrayAdapter<>(getActivity(), R.layout.view_spinner_dropdown, locations);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setEnabled(true);
            spinner.setAdapter(adapter);
        } else {
            resetLocationSpinner(spinner, R.array.spinner_error_loading_states);
            Toast.makeText(getActivity(), R.string.error_no_internet, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}

    private void updateCountries(List<CountryInfo> data) {
        ArrayAdapter<CountryInfo> localeAdapter = new ArrayAdapter<>(getActivity(), R.layout.view_spinner_dropdown, data);
        localeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        country.setAdapter(localeAdapter);
        country.setEnabled(true);

        int position = countryInfo != null ? data.indexOf(countryInfo) : 0;
        country.setSelection(position);

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
            Object item = country.getSelectedItem();
            if(item instanceof CountryInfo) {
                countryInfo = (CountryInfo) item;
                resetLocationSpinner(state, R.array.spinner_loading);
                resetLocationSpinner(district, R.array.spinner_loading);
                loadStatesForCountry(countryInfo);
            }
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

    private void loadStatesForCountry(CountryInfo countryInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_COUNTRY_INFO, countryInfo);

        Loader loader = getLoaderManager().getLoader(LOAD_STATES_ID);
        if(loader != null && ((LocationInfoLoader)loader).getCountryInfo().equals(countryInfo)) {
            getLoaderManager().initLoader(LOAD_STATES_ID, bundle, this);
        } else {
            getLoaderManager().restartLoader(LOAD_STATES_ID, bundle, this);
        }
    }

    private View.OnClickListener onBirthdayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setOnDateSetListener(UserInfoBaseFragment.this);
            datePickerFragment.show(getFragmentManager(), "datePickerFragment");
        }
    };

    private AdapterView.OnItemSelectedListener onStateSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Location) {
                updateDistrictSpinnerForState((Location) item);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    public abstract void onCountriesLoaded(List<CountryInfo> data);

    public abstract void onStatesLoaded(List<Location> locations);

}
