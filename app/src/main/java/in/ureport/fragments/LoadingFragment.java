package in.ureport.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class LoadingFragment extends Fragment {

    private static final String BUNDLE_LOADING_KEY = "loading";

    private ProgressDialog progressDialog;
    private boolean loading;

    abstract protected String getLoadingMessage();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getLoadingMessage());
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
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
            loading = savedInstanceState.getBoolean(BUNDLE_LOADING_KEY);

        if (loading) showLoading();
    }

    protected void showLoading() {
        loading = true;
        if (!isDetached())
            progressDialog.show();
    }

    protected void dismissLoading() {
        loading = false;
        if (!isDetached())
            progressDialog.dismiss();
    }

}
