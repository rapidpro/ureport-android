package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.ilhasoft.support.tool.EditTextValidator;
import in.ureport.R;
import in.ureport.models.User;
import in.ureport.models.holders.UserGender;
import in.ureport.models.holders.UserLocale;
import in.ureport.tasks.ListCountriesTask;
import in.ureport.tasks.SaveUserTask;

/**
 * Created by ilhasoft on 7/9/15.
 */
public class SignUpFragment extends Fragment {

    private static final int FIELDS_MINIMUM_SIZE = 5;

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText birthday;
    private Spinner country;
    private Spinner gender;

    private EditTextValidator validator = new EditTextValidator();

    private LoginFragment.LoginListener loginListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        username = (EditText) view.findViewById(R.id.username);
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        birthday = (EditText) view.findViewById(R.id.birthday);

        gender = (Spinner) view.findViewById(R.id.gender);
        loadUserGenders();

        country = (Spinner) view.findViewById(R.id.country);
        loadCountryList();

        Button confirm = (Button) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(onConfirmClickListener);
    }

    private void loadUserGenders() {
        List<UserGender> userGenders = getUserGenders();

        ArrayAdapter<UserGender> genderAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, userGenders);
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
        ListCountriesTask listCountriesTask = new ListCountriesTask() {
            @Override
            protected void onPostExecute(List<UserLocale> userLocales) {
                super.onPostExecute(userLocales);

                ArrayAdapter<UserLocale> localeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, userLocales);
                localeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                country.setAdapter(localeAdapter);
                selectCurrentUserLocale(userLocales);
            }
        };
        listCountriesTask.execute();
    }

    private void selectCurrentUserLocale(List<UserLocale> userLocales) {
        Locale locale = Locale.getDefault();
        UserLocale userLocale = new UserLocale(locale);
        int userLocalePosition = userLocales.indexOf(userLocale);

        if(userLocalePosition >= 0) {
            country.setSelection(userLocalePosition);
        }
    }

    private boolean isFieldsValid() {
        String messageNameValidation = getString(R.string.error_minimum_size
                , FIELDS_MINIMUM_SIZE);

        boolean validTextFields = validator.validateSizeMulti(FIELDS_MINIMUM_SIZE
                , messageNameValidation, username, password);
        boolean validEmail = validator.validateEmail(email, getString(R.string.error_valid_email));
        boolean validBirthday = validator.validateEmpty(birthday, getString(R.string.error_required_field));

        return validTextFields && validEmail && validBirthday &&
                isSpinnerValid(country) && isSpinnerValid(gender);
    }

    private boolean isSpinnerValid(Spinner spinner) {
        return spinner.getSelectedItemPosition() != Spinner.INVALID_POSITION;
    }

    public void setLoginListener(LoginFragment.LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    @NonNull
    private User createUser() {
        User user = new User();
        user.setUsername(username.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());

        UserLocale userLocale = (UserLocale) country.getAdapter().getItem(country.getSelectedItemPosition());
        String displayCountry = userLocale.getLocale().getDisplayCountry();
        user.setCountry(displayCountry);

        UserGender userGender = (UserGender)gender.getAdapter().getItem(gender.getSelectedItemPosition());
        user.setGender(userGender.getGender());
        return user;
    }

    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isFieldsValid()) {
                User user = createUser();

                SaveUserTask saveUserTask = new SaveUserTask();
                saveUserTask.execute(user);

                if(loginListener != null) {
                    loginListener.userReady(user);
                }
            }
        }
    };
}
