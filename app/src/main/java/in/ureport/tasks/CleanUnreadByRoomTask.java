package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import in.ureport.db.business.ChatNotificationBusiness;
import in.ureport.db.repository.ChatNotificationRepository;
import in.ureport.models.ChatRoom;
import in.ureport.models.db.ChatNotification;

/**
 * Created by johncordeiro on 22/08/15.
 */
public class CleanUnreadByRoomTask extends NotificationTask<ChatRoom, Void, Void> {

    private static final String TAG = "CleanUnreadByRoomTask";
    public static final String CLEAN_UNREAD_TYPE = "cleanUnreadNotification";

    public CleanUnreadByRoomTask(Context context) {
        super(context);
    }

    @Override
    protected String getNotificationType() {
        return CLEAN_UNREAD_TYPE;
    }

    @Override
    protected Void doInBackground(ChatRoom... params) {
        try {
            ChatNotificationRepository repository = new ChatNotificationBusiness();
            repository.deleteByChatRoomId(params[0].getKey());
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }

        return null;
    }
}
