package in.ureport.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.ilhasoft.support.tool.EditTextValidator;
import br.com.ilhasoft.support.widget.DatePickerFragment;
import in.ureport.R;
import in.ureport.models.User;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.holders.UserGender;

/**
 * Created by johncordeiro on 10/09/15.
 */
public abstract class UserInfoBaseFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "UserInfoBaseFragment";

    protected static final String EXTRA_USER = "user";
    private static final String EXTRA_COUNTRY_INFO = "countryInfo";

    public static final int FIELDS_MINIMUM_SIZE = 5;

    protected User user;
    protected User.Type userType = User.Type.ureport;

    private EditTextValidator validator;
    private DateFormat birthdayFormatter;

    protected Toolbar toolbar;
    protected EditText username;
    protected EditText email;
    protected EditText password;
    protected EditText birthday;
    protected Spinner gender;
    protected Button confirm;

    protected CountryInfo countryInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCountryInfoFromState(savedInstanceState);
        getUserFromArguments();
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
            setEditTextDate(birthday, new Date(user.getBirthday()));
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
        UserGender userGender = new UserGender(getActivity(), user.getGenderAsEnum());
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

        return validTextFields && validEmail && validBirthday && isSpinnerValid(gender) &&
                validatePasswordIfNeeded();
    }

    protected boolean isBirthdayValid() {
        return validator.validateEmpty(birthday, getString(R.string.error_required_field));
    }

    protected boolean isUsernameValid() {
        String messageNameValidation = getString(R.string.error_required_field);
        return validator.validateEmpty(username, messageNameValidation);
    }

    protected boolean isEmailValid() {
        return validator.validateEmail(email, getString(R.string.error_valid_email));
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
//        return (CountryInfo) country.getAdapter().getItem(country.getSelectedItemPosition());
        return new CountryInfo();
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

    private View.OnClickListener onBirthdayClickListener = view -> {
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(Calendar.getInstance().getTime());
        datePickerFragment.setOnDateSetListener(UserInfoBaseFragment.this);
        datePickerFragment.show(getFragmentManager(), "datePickerFragment");
    };
}
