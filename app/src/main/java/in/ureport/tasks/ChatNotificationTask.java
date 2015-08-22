package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;
import java.util.List;

import in.ureport.db.business.ChatNotificationBusiness;
import in.ureport.db.repository.ChatNotificationRepository;
import in.ureport.helpers.SystemHelper;
import in.ureport.managers.LocalNotificationManager;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.User;
import in.ureport.models.db.ChatNotification;

/**
 * Created by johncordeiro on 22/08/15.
 */
public class ChatNotificationTask extends AsyncTask<ChatMessage, Void, Void> {

    private static final String TAG = "ChatNotificationTask";

    private final Context context;
    private final ChatRoom chatRoom;

    public ChatNotificationTask(Context context, ChatRoom chatRoom) {
        this.context = context;
        this.chatRoom = chatRoom;
    }

    @Override
    protected Void doInBackground(ChatMessage... params) {
        try {
            ChatMessage chatMessage = params[0];

            ChatNotificationRepository repository = new ChatNotificationBusiness();
            List<ChatNotification> chatNotificationList = repository.getAllOrderedByDate();

            ChatNotification chatNotification = buildNotification(chatMessage);
            chatNotification.save();

            chatNotificationList.add(0, chatNotification);

            SystemHelper systemHelper = new SystemHelper();
            if(!systemHelper.isAppRunning(context)) {
                LocalNotificationManager localNotificationManager = new LocalNotificationManager(context);
                localNotificationManager.sendChatListNotification(chatNotificationList);
            }
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

    @NonNull
    private ChatNotification buildNotification(ChatMessage chatMessage) {
        User user = chatMessage.getUser();
        return new ChatNotification(chatRoom.getKey(), user.getNickname()
                    , chatMessage.getMessage(), new Date());
    }
}
