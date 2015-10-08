package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import in.ureport.managers.NotificationListManager;
import in.ureport.models.Notification;

/**
 * Created by johncordeiro on 21/07/15.
 */
public class NotificationLoader extends AsyncTaskLoader<List<Notification>> {

    private NotificationListManager notificationListManager;

    public NotificationLoader(Context context) {
        super(context);
        notificationListManager = new NotificationListManager(getContext());
    }

    @Override
    public List<Notification> loadInBackground() {
        List<Notification> notifications = new ArrayList<>();
        notifications.addAll(notificationListManager.getNotificationsByMessages());
        notifications.addAll(notificationListManager.getNotificationsByContributions());
        notifications.addAll(notificationListManager.getNotificationsByChat());

        return notifications;
    }

}
