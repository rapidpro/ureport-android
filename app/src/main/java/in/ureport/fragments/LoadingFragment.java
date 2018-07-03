package in.ureport.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class LoadingFragment extends Fragment {

    private static final String BUNDLE_LOADING_KEY = "loading";
    private static final String BUNDLE_LOADING_MESSAGE_KEY = "lastMessage";

    private ProgressDialog progressDialog;
    private boolean loading;
    private String loadingMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_LOADING_KEY, loading);
        outState.putString(BUNDLE_LOADING_MESSAGE_KEY, loadingMessage);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            loading = savedInstanceState.getBoolean(BUNDLE_LOADING_KEY);
            setLoadingMessage(savedInstanceState.getString(BUNDLE_LOADING_MESSAGE_KEY));
        }
        if (loading)
            showLoading();
    }

    protected void setLoadingMessage(String message) {
        loadingMessage = message;
        progressDialog.setMessage(message);
    }

    protected void showLoading() {
        loading = true;
        if (isAdded()) progressDialog.show();
    }

    protected void dismissLoading() {
        loading = false;
        if (isAdded()) progressDialog.dismiss();
    }

}
