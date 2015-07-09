package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import br.com.ilhasoft.support.tool.EditTextValidator;
import in.ureport.R;

/**
 * Created by ilhasoft on 7/7/15.
 */
public class CredentialsLoginFragment extends Fragment {

    private EditText username;
    private EditText password;

    private EditTextValidator validator = new EditTextValidator();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credentials_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);

        Button login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(onLoginClickListener);

        setupToolbar(view);
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

    private boolean validateFields() {
        boolean validUsername = validator.validateEmpty(username, getString(R.string.error_required_field));
        boolean validPassword = validator.validateEmpty(password, getString(R.string.error_required_field));

        return validUsername && validPassword;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    private View.OnClickListener onLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (validateFields()) {

            }
        }
    };
}
