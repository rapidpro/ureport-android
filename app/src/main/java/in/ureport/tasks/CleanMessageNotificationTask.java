package in.ureport.tasks;

import android.content.Context;

import br.com.ilhasoft.support.db.business.AbstractBusiness;
import br.com.ilhasoft.support.db.repository.AbstractRepository;
import in.ureport.managers.LocalNotificationManager;
import in.ureport.models.db.MessageNotification;

/**
 * Created by johncordeiro on 07/10/15.
 */
public class CleanMessageNotificationTask extends NotificationTask<Void, Void, Void> {

    public CleanMessageNotificationTask(Context context) {
        super(context);
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
