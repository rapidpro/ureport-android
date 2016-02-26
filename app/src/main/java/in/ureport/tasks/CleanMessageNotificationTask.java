package in.ureport.tasks;

import android.content.Context;

import in.ureport.db.business.AbstractBusiness;
import in.ureport.db.repository.AbstractRepository;
import in.ureport.managers.LocalNotificationManager;
import in.ureport.models.db.MessageNotification;

/**
 * Created by johncordeiro on 07/10/15.
 */
public class CleanMessageNotificationTask extends NotificationTask<Void, Void, Void> {

    public static final String CLEAN_MESSAGE_TYPE = "cleanMessageNotification";

    public CleanMessageNotificationTask(Context context) {
        super(context);
    }

    @Override
    protected String getNotificationType() {
        return CLEAN_MESSAGE_TYPE;
    }

    @Override
    protected Void doInBackground(Void... params) {
        AbstractRepository<MessageNotification> repository = new AbstractBusiness<>(MessageNotification.class);
        repository.deleteAll();

        LocalNotificationManager localNotificationManager = new LocalNotificationManager(context);
        localNotificationManager.cancelMessageNotification();
        return null;
    }

}
