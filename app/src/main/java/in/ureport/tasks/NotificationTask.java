package in.ureport.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import in.ureport.activities.BaseActivity;

/**
 * Created by johncordeiro on 07/10/15.
 */
public abstract class NotificationTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {

    public static final String EXTRA_TYPE = "type";
    protected Context context;

    public NotificationTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        String type = getNotificationType();

        Intent reloadNotificationIntent = new Intent(BaseActivity.ACTION_RELOAD_NOTIFICATIONS);
        if(type != null)
            reloadNotificationIntent.putExtra(EXTRA_TYPE, type);
        context.sendBroadcast(reloadNotificationIntent);
    }

    protected abstract String getNotificationType();
}
