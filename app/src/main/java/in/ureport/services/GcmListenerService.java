package in.ureport.services;

import android.os.Bundle;
import android.util.Log;

import br.com.ilhasoft.support.json.JsonDeserializer;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.FlowManager;
import in.ureport.managers.GcmTopicManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.network.GcmServices;
import in.ureport.tasks.ChatNotificationTask;
import in.ureport.tasks.ContributionNotificationTask;
import in.ureport.tasks.MessageNotificationTask;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private static final String TAG = "GcmListenerService";

    private static final String EXTRA_CHAT_ROOM = "chatRoom";
    private static final String EXTRA_CHAT_MESSAGE = "chatMessage";

    private static final String EXTRA_NOTIFICATION_TYPE = "type";
    private static final String EXTRA_MESSAGE = "message";

    private static final String EXTRA_CONTRIBUTION = "contribution";
    private static final String EXTRA_STORY = "story";

    public enum Type {
        Rapidpro,
        Chat,
        Contribution
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        if(from.startsWith(GcmTopicManager.CHAT_TOPICS_PATH) || hasType(data, Type.Chat)) {
            sendChatMessageNotification(data);
        } else if(from.startsWith(GcmTopicManager.STORY_TOPICS_PATH) || hasType(data, Type.Contribution)) {
            sendContributionNotification(data);
        } else {
            handleNotificationType(data);
        }
    }

    private boolean hasType(Bundle data, Type type) {
        return data != null && data.containsKey(EXTRA_NOTIFICATION_TYPE) && data.getString(EXTRA_NOTIFICATION_TYPE).equals(type.toString());
    }

    private void sendContributionNotification(Bundle data) {
        try {
            Contribution contribution = getObject(data, EXTRA_CONTRIBUTION, Contribution.class);
            Story story = getObject(data, EXTRA_STORY, Story.class);

            if(isUserAllowedForMessageNotification(contribution.getAuthor())) {
                ContributionNotificationTask contributionNotificationTask = new ContributionNotificationTask(this, story);
                contributionNotificationTask.execute(contribution);
            }
        } catch(Exception exception) {
            Log.e(TAG, "sendChatMessageNotification ", exception);
        }
    }

    private void handleNotificationType(Bundle data) {
        try {
            String notificationType = data.getString(EXTRA_NOTIFICATION_TYPE);
            Type type = Type.valueOf(notificationType);

            switch(type) {
                case Rapidpro:
                    sendRapidproNotification(data);
            }
        } catch(Exception exception) {
            Log.e(TAG, "onMessageReceived ", exception);
        }
    }

    private void sendRapidproNotification(Bundle data) {
        String message = data.getString(EXTRA_MESSAGE);

        if(FlowManager.canShowNextNotification()) {
            MessageNotificationTask messageNotificationTask = new MessageNotificationTask(this);
            messageNotificationTask.execute(message);
        }
    }

    private void sendChatMessageNotification(Bundle data) {
        try {
            ChatRoom chatRoom = getObject(data, EXTRA_CHAT_ROOM, ChatRoom.class);
            ChatMessage chatMessage = getObject(data, EXTRA_CHAT_MESSAGE, ChatMessage.class);

            if(isUserAllowedForMessageNotification(chatMessage.getUser())) {
                ChatNotificationTask chatNotificationTask = new ChatNotificationTask(this, chatRoom);
                chatNotificationTask.execute(chatMessage);
            }
        } catch(Exception exception) {
            Log.e(TAG, "sendChatMessageNotification ", exception);
        }
    }

    private boolean isUserAllowedForMessageNotification(User user) {
        FirebaseManager.init(this);
        String authUserKey = UserManager.getUserId();
        return authUserKey != null && !user.getKey().equals(authUserKey);
    }

    private <T> T getObject(Bundle data, String key, Class<T> mClass) {
        String json = data.getString(key);
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(json);
        deserializer.setDateFormat(GcmServices.DATE_STYLE);
        return deserializer.get(mClass);
    }
}
