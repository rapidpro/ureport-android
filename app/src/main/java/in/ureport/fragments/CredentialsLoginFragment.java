package in.ureport.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import br.com.ilhasoft.support.tool.EditTextValidator;
import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.helpers.ToolbarDesigner;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.models.User;
import in.ureport.models.holders.Login;
import in.ureport.network.UserServices;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by johncordeiro on 7/7/15.
 */
public class CredentialsLoginFragment extends ProgressFragment {

    private EditText email;
    private EditText password;
    private CheckBox checkBox;
    private SharedPreferences loginPreferences;

    private EditTextValidator validator = new EditTextValidator();

    private LoginFragment.LoginListener loginListener;

    private FirebaseAuth firebaseAuth;
    private static OnCompleteListener<AuthResult> authResultListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupContextDependencies();
        return inflater.inflate(R.layout.fragment_credentials_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        setLoadingMessage(getString(R.string.load_message_logging));
    }

    @Override
    public void onResume() {
        super.onResume();
        setLoginStatusBarColor();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof LoginFragment.LoginListener) {
            loginListener = (LoginFragment.LoginListener) activity;
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

    private void setLoginStatusBarColor() {
        StatusBarDesigner statusBarDesigner = new StatusBarDesigner();
        statusBarDesigner.setStatusBarColorById(getActivity(), R.color.fun_green);
    }

    private void setupContextDependencies() {
        authResultListener = task -> {
            dismissLoading();
            if (task.isSuccessful() && task.getResult() != null) {
                getUserInfoAndContinue(task.getResult());
            } else {
                showLoginError();
            }
        };
    }

    private void setupView(View view) {
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);

        checkBox = (CheckBox) view.findViewById(R.id.rememberMe);
        loginPreferences = getContext().getSharedPreferences("loginPreferences", MODE_PRIVATE);

        TextView forgotPassword = (TextView) view.findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(onForgotPasswordClickListener);

        Button login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(onLoginClickListener);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        ToolbarDesigner toolbarDesigner = new ToolbarDesigner();
        toolbarDesigner.setupFragmentDefaultToolbar(toolbar, R.string.label_login, this);

        checkRememberMeOption();
    }

    private void checkRememberMeOption() {
        if (loginPreferences.getBoolean("rememberMe", false)) {
            email.setText(loginPreferences.getString("email", ""));
            checkBox.setChecked(true);
        }
    }

    private boolean validateFields() {
        boolean validEmail = validator.validateEmpty(email, getString(R.string.error_required_field));
        boolean validPassword = validator.validateEmpty(password, getString(R.string.error_required_field));

        return validEmail && validPassword;
    }

    private void login(Login login) {
        saveLoginPreferences(login);
        showLoading();
        firebaseAuth.signInWithEmailAndPassword(login.getEmail(), login.getPassword())
                .addOnCompleteListener(task -> authResultListener.onComplete(task));
    }

    private void saveLoginPreferences(Login login) {
        SharedPreferences.Editor loginPreferencesEditor = loginPreferences.edit();
        if (checkBox.isChecked()) {
            loginPreferencesEditor.putBoolean("rememberMe", true);
            loginPreferencesEditor.putString("email", login.getEmail());
            loginPreferencesEditor.apply();
        } else {
            loginPreferencesEditor.clear();
            loginPreferencesEditor.commit();
        }
    }

    private void getUserInfoAndContinue(final AuthResult authResult) {
        final UserServices userServices = new UserServices();
        userServices.getUser(authResult.getUser().getUid(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                User user = dataSnapshot.getValue(User.class);
                loginListener.onUserReady(user, false);
            }
        });
    }

    private void showLoginError() {
        Toast.makeText(getContext(), R.string.message_invalid_login, Toast.LENGTH_LONG).show();
    }

    private View.OnClickListener onLoginClickListener = view -> {
        if (validateFields()) {
            Login login = new Login(email.getText().toString(), password.getText().toString());
            login(login);
        }
    };

    private View.OnClickListener onForgotPasswordClickListener = view -> {
        if (loginListener != null)
            loginListener.onForgotPassword();
    };

}