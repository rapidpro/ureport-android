package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.ureport.R;
import in.ureport.managers.UserManager;
import in.ureport.models.Notification;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 21/07/15.
 */
public class NotificationLoader extends AsyncTaskLoader<List<Notification>> {

    public NotificationLoader(Context context) {
        super(context);
    }

    @Override
    public List<Notification> loadInBackground() {
        List<User> users = UserManager.getFakeUsers(getContext());
        List<Notification> notifications = new ArrayList<>();

        Notification notification = new Notification();
        notification.setMessage(getContext().getString(R.string.notification_message1, users.get(3).getNickname()));
        notification.setDate(new Date());
        notification.setUser(users.get(3));

        Notification notification2 = new Notification();
        notification2.setMessage(getContext().getString(R.string.notification_message2));
        notification2.setDate(new Date());

        Notification notification3 = new Notification();
        notification3.setMessage(getContext().getString(R.string.notification_message3));
        notification3.setDate(new Date());

        notifications.add(notification);
        notifications.add(notification2);
        notifications.add(notification3);

        return notifications;
    }
}
