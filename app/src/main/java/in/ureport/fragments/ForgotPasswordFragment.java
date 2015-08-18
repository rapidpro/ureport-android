package in.ureport.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import br.com.ilhasoft.support.tool.EditTextValidator;
import in.ureport.R;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.ToolbarDesigner;

/**
 * Created by johncordeiro on 17/08/15.
 */
public class ForgotPasswordFragment extends Fragment {

    private EditText email;

    private LoginFragment.LoginListener loginListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof LoginFragment.LoginListener) {
            loginListener = (LoginFragment.LoginListener)activity;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        email = (EditText)view.findViewById(R.id.email);

        Button send = (Button) view.findViewById(R.id.send);
        send.setOnClickListener(onSendClickListener);

        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        ToolbarDesigner toolbarDesigner = new ToolbarDesigner();
        toolbarDesigner.setupFragmentDefaultToolbar(toolbar, R.string.label_forgot_password, this);
    }

    private View.OnClickListener onSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validateFields()) {
                String email = ForgotPasswordFragment.this.email.getText().toString();
                
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null
                        , getString(R.string.load_message_reset_password), true, false);

                FirebaseManager.getReference().resetPassword(email, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        if(loginListener != null)
                            loginListener.onPasswordReset();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), R.string.error_forgot_password, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };

    private boolean validateFields() {
        EditTextValidator validator = new EditTextValidator();
        return validator.validateEmail(email, getString(R.string.error_valid_email));
    }
}
