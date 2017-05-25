package in.ureport.services;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import br.com.ilhasoft.support.json.JsonDeserializer;
import in.ureport.R;
import in.ureport.managers.FirebaseProxyManager;
import in.ureport.managers.FcmTopicManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.models.gcm.Type;
import in.ureport.network.FcmServices;
import in.ureport.tasks.ChatNotificationTask;
import in.ureport.tasks.ContributionNotificationTask;
import io.rapidpro.sdk.services.FcmClientIntentService;

/**
 * Created by John Cordeiro on 5/19/17.
 * Copyright © 2017 ureport-android, Inc. All rights reserved.
 */

public class UreportFcmReceivedListener extends FcmClientIntentService {

    private static final String TAG = "UreportFcmReceived";

    private static final String EXTRA_CHAT_ROOM = "chatRoom";
    private static final String EXTRA_CHAT_MESSAGE = "chatMessage";

    private static final String EXTRA_NOTIFICATION_TYPE = "type";
    private static final String EXTRA_CONTRIBUTION = "contribution";
    private static final String EXTRA_STORY = "story";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();

        if(from.startsWith(FcmTopicManager.CHAT_TOPICS_PATH) || hasType(data, Type.Chat)) {
            sendChatMessageNotification(data);
        } else if(from.startsWith(FcmTopicManager.STORY_TOPICS_PATH) || hasType(data, Type.Contribution)) {
            sendContributionNotification(data);
        }
    }

    @Override
    protected void onCreateLocalNotification(NotificationCompat.Builder builder) {
        builder.setSmallIcon(R.drawable.icon_notification);
        super.onCreateLocalNotification(builder);
    }

    private boolean hasType(Map<String, String> data, Type type) {
        return data != null && data.containsKey(EXTRA_NOTIFICATION_TYPE) && data.get(EXTRA_NOTIFICATION_TYPE).equals(type.toString());
    }

    private void sendContributionNotification(Map<String, String> data) {
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

    private void sendChatMessageNotification(Map<String, String> data) {
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
        FirebaseProxyManager.init(this);
        String authUserKey = UserManager.getUserId();
        return authUserKey != null && !user.getKey().equals(authUserKey);
    }

    private <T> T getObject(Map<String, String> data, String key, Class<T> mClass) {
        String json = data.get(key);
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(json);
        deserializer.setDateFormat(FcmServices.DATE_STYLE);
        return deserializer.get(mClass);
    }
}
