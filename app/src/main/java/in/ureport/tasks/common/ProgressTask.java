package in.ureport.tasks.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.util.Log;

/**
 * Created by johncordeiro on 13/08/15.
 */
public abstract class ProgressTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private static final String TAG = "ProgressTask";
    private static final int NO_PROGRESS_TEXT = -1;

    protected final Context context;
    private final @StringRes int text;

    private ProgressDialog progressDialog;

    public ProgressTask(Context context, @StringRes int text) {
        this.context = context;
        this.text = text;
    }

    public ProgressTask(Context context) {
        this.context = context;
        this.text = NO_PROGRESS_TEXT;
    }

    @Override
    protected void onPreExecute() {
        if(showProgress())
            progressDialog = ProgressDialog.show(context, null
                , context.getString(text), true, false);
    }

    @Override
    protected void onPostExecute(Result result) {
        if(showProgress())
            progressDialog.dismiss();
    }

    protected void setException(final Exception exception) {
        progressDialog.dismiss();
        cancel(true);

        runListenerOnUiThread(exception);
    }

    private void runListenerOnUiThread(final Exception exception) {
        Handler handler = new Handler(context.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                onTaskException(exception);
            }
        });
    }

    public void onTaskException(Exception exception) {
        Log.e(TAG, "An exception occurred during the task progress ", exception);
    }

    private boolean showProgress() {
        return this.text != NO_PROGRESS_TEXT;
    }

    public Context getContext() {
        return context;
    }
}
