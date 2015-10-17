package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Date;

import in.ureport.managers.LocalNotificationManager;
import in.ureport.models.db.MessageNotification;

/**
 * Created by johncordeiro on 07/10/15.
 */
public class MessageNotificationTask extends NotificationTask<String, Void, Void> {

    private static final String TAG = "MessageNotification";
    public static final String NEW_MESSAGE_TYPE = "newMessageNotification";

    public MessageNotificationTask(Context context) {
        super(context);
    }

    @Override
    protected String getNotificationType() {
        return NEW_MESSAGE_TYPE;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            String message = params[0];

            MessageNotification messageNotification = new MessageNotification(message, new Date());
            messageNotification.save();

            LocalNotificationManager localNotificationManager = new LocalNotificationManager(context);
            localNotificationManager.sendMessageNotification(message);
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

}
