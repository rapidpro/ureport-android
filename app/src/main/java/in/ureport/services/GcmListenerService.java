package in.ureport.services;

import android.os.Bundle;
import android.util.Log;

import br.com.ilhasoft.support.json.JsonDeserializer;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.GcmTopicManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.network.GcmServices;
import in.ureport.tasks.ChatNotificationTask;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private static final String TAG = "GcmListenerService";

    private static final String EXTRA_CHAT_ROOM = "chatRoom";
    private static final String EXTRA_CHAT_MESSAGE = "chatMessage";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        if(from.startsWith(GcmTopicManager.TOPICS_PATH)) {
            sendChatMessageNotification(data);
        }
    }

    private void sendChatMessageNotification(Bundle data) {
        try {
            ChatRoom chatRoom = getChatRoom(data);
            ChatMessage chatMessage = getChatMessage(data);

            if(isUserAllowedForMessageNotification(chatMessage)) {
                ChatNotificationTask chatNotificationTask = new ChatNotificationTask(this, chatRoom);
                chatNotificationTask.execute(chatMessage);
            }
        } catch(Exception exception) {
            Log.e(TAG, "sendChatMessageNotification ", exception);
        }
    }

    private boolean isUserAllowedForMessageNotification(ChatMessage chatMessage) {
        FirebaseManager.init(this);
        String authUserKey = UserManager.getUserId();
        return authUserKey != null && !chatMessage.getUser().getKey().equals(authUserKey);
    }

    private ChatMessage getChatMessage(Bundle data) {
        String chatMessageJson = data.getString(EXTRA_CHAT_MESSAGE);
        JsonDeserializer<ChatMessage> chatMessageDeserializer = new JsonDeserializer<>(chatMessageJson);
        chatMessageDeserializer.setDateFormat(GcmServices.DATE_STYLE);
        return chatMessageDeserializer.get(ChatMessage.class);
    }

    private ChatRoom getChatRoom(Bundle data) {
        String chatRoomJson = data.getString(EXTRA_CHAT_ROOM);

        JsonDeserializer<GroupChatRoom> chatRoomDeserializer = new JsonDeserializer<>(chatRoomJson);
        chatRoomDeserializer.setDateFormat(GcmServices.DATE_STYLE);
        return chatRoomDeserializer.get(GroupChatRoom.class);
    }
}
