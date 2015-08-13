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

    protected final Context context;
    private final @StringRes int text;

    private ProgressDialog progressDialog;

    public ProgressTask(Context context, @StringRes int text) {
        this.context = context;
        this.text = text;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, null
                , context.getString(text), true, false);
    }

    @Override
    protected void onPostExecute(Result result) {
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
}
