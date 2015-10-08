package in.ureport.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import in.ureport.activities.BaseActivity;

/**
 * Created by johncordeiro on 07/10/15.
 */
public abstract class NotificationTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {

    protected Context context;

    public NotificationTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        Intent reloadNotificationIntent = new Intent(BaseActivity.ACTION_RELOAD_NOTIFICATIONS);
        context.sendBroadcast(reloadNotificationIntent);
    }
}
