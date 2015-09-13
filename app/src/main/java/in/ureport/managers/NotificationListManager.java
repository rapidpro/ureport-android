package in.ureport.managers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import in.ureport.R;
import in.ureport.activities.ChatRoomActivity;
import in.ureport.db.business.ChatNotificationBusiness;
import in.ureport.db.repository.ChatNotificationRepository;
import in.ureport.listener.OnNotificationSelectedListener;
import in.ureport.models.User;
import in.ureport.models.db.ChatNotification;
import in.ureport.models.Notification;

/**
 * Created by johncordeiro on 12/09/15.
 */
public class NotificationListManager {

    private Context context;

    public NotificationListManager(Context context) {
        this.context = context;
    }

    public List<Notification> getNotificationsByChatNotifications() {
        List<Notification> notifications = new ArrayList<>();

        ChatNotificationRepository chatNotificationRepository = new ChatNotificationBusiness();
        List<ChatNotification> chatNotificationList = chatNotificationRepository.getAllOrderedByDate();

        LinkedHashMap<ChatNotification, Integer> chatNotificationMessages = getChatNotificationCount(chatNotificationList);

        Set<ChatNotification> chatNotificationSet = chatNotificationMessages.keySet();
        for (final ChatNotification chatNotification : chatNotificationSet) {
            Notification notification = buildNotificationByChatNotification(chatNotificationMessages, chatNotification);
            notification.setOnNotificationSelectedListener(new OnChatNotificationSelectedListener(chatNotification));
            notifications.add(notification);
        }

        return notifications;
    }

    @NonNull
    private Notification buildNotificationByChatNotification(LinkedHashMap<ChatNotification, Integer> chatNotificationMessages, ChatNotification chatNotification) {
        User user = buildUserByChatNotification(chatNotification);
        String message = buildMessageByChatNotification(chatNotificationMessages, chatNotification);
        return new Notification(chatNotification.getChatRoomId(), message, chatNotification.getDate(), user);
    }

    @NonNull
    private String buildMessageByChatNotification(LinkedHashMap<ChatNotification, Integer> chatNotificationMessages, ChatNotification chatNotification) {
        int quantity = chatNotificationMessages.get(chatNotification);

        return String.format(context.getResources().getQuantityString(R.plurals.notification_chat_message, quantity)
                , chatNotification.getNickname(), quantity);
    }

    @NonNull
    private User buildUserByChatNotification(ChatNotification chatNotification) {
        User user = new User();
        user.setNickname(chatNotification.getNickname());
        user.setPicture(chatNotification.getPicture());
        return user;
    }

    @NonNull
    private LinkedHashMap<ChatNotification, Integer> getChatNotificationCount(List<ChatNotification> chatNotificationList) {
        LinkedHashMap<ChatNotification, Integer> chatNotificationMessages = new LinkedHashMap<>();

        for (ChatNotification chatNotification : chatNotificationList) {
            Integer count = chatNotificationMessages.get(chatNotification);
            count = count != null ? count+1 : 1;

            chatNotificationMessages.put(chatNotification, count);
        }
        return chatNotificationMessages;
    }

    private class OnChatNotificationSelectedListener implements OnNotificationSelectedListener {

        private ChatNotification chatNotification;

        public OnChatNotificationSelectedListener(ChatNotification chatNotification) {
            this.chatNotification = chatNotification;
        }

        @Override
        public void onNotificationSelected(Notification notification) {
            Intent openChatIntent = new Intent(context.getApplicationContext(), ChatRoomActivity.class);
            openChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openChatIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM_KEY, chatNotification.getChatRoomId());
            context.getApplicationContext().startActivity(openChatIntent);
        }
    }
}
