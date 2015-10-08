package in.ureport.managers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import br.com.ilhasoft.support.db.business.AbstractBusiness;
import br.com.ilhasoft.support.db.repository.AbstractRepository;
import in.ureport.R;
import in.ureport.activities.ChatRoomActivity;
import in.ureport.activities.MainActivity;
import in.ureport.activities.StoryViewActivity;
import in.ureport.db.business.ChatNotificationBusiness;
import in.ureport.db.business.ContributionNotificationBusiness;
import in.ureport.db.repository.ChatNotificationRepository;
import in.ureport.db.repository.ContributionNotificationRepository;
import in.ureport.listener.OnNotificationSelectedListener;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.models.db.ChatNotification;
import in.ureport.models.Notification;
import in.ureport.models.db.ContributionNotification;
import in.ureport.models.db.MessageNotification;

/**
 * Created by johncordeiro on 12/09/15.
 */
public class NotificationListManager {

    private Context context;

    public NotificationListManager(Context context) {
        this.context = context;
    }

    public List<Notification> getNotificationsByContributions() {
        List<Notification> notifications = new ArrayList<>();

        ContributionNotificationRepository repository = new ContributionNotificationBusiness();
        List<ContributionNotification> contributionNotifications = repository.getAllOrderedByDate();

        for (ContributionNotification contributionNotification : contributionNotifications) {
            Notification notification = new Notification();
            notification.setId(contributionNotification.getStoryId());
            notification.setMessage(contributionNotification.getMessage());
            notification.setDate(contributionNotification.getDate());
            notification.setUser(buildUserForNotification(contributionNotification.getNickname(), contributionNotification.getPicture()));
            notification.setOnNotificationSelectedListener(new OnContributionNotificationSelectedListener(contributionNotification));

            notifications.add(notification);
        }

        return notifications;
    }

    public List<Notification> getNotificationsByMessages() {
        List<Notification> notifications = new ArrayList<>();

        AbstractRepository<MessageNotification> repository = new AbstractBusiness<>(MessageNotification.class);
        List<MessageNotification> messageNotifications = repository.getAll();

        for (MessageNotification messageNotification : messageNotifications) {
            Notification notification = new Notification();
            notification.setId(LocalNotificationManager.Type.Message.toString());
            notification.setMessage(messageNotification.getMessage());
            notification.setDate(messageNotification.getDate());
            notification.setOnNotificationSelectedListener(new OnMessageNotificationSelectedListener());

            notifications.add(notification);
        }

        return notifications;
    }

    public List<Notification> getNotificationsByChat() {
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
        User user = buildUserForNotification(chatNotification.getNickname(), chatNotification.getPicture());
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
    private User buildUserForNotification(String nickname, String picture) {
        User user = new User();
        user.setNickname(nickname);
        user.setPicture(picture);
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

    private class OnMessageNotificationSelectedListener implements OnNotificationSelectedListener {

        @Override
        public void onNotificationSelected(Notification notification) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setAction(MainActivity.ACTION_OPEN_MESSAGE_NOTIFICATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        }
    }

    private class OnContributionNotificationSelectedListener implements OnNotificationSelectedListener {

        private ContributionNotification contributionNotification;

        public OnContributionNotificationSelectedListener(ContributionNotification contributionNotification) {
            this.contributionNotification = contributionNotification;
        }

        @Override
        public void onNotificationSelected(Notification notification) {
            CountryProgramManager.switchToUserCountryProgram();

            Intent storyViewIntent = new Intent(context.getApplicationContext(), StoryViewActivity.class);
            storyViewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            storyViewIntent.setAction(StoryViewActivity.ACTION_LOAD_STORY);

            storyViewIntent.putExtra(StoryViewActivity.EXTRA_STORY, buildStory());
            storyViewIntent.putExtra(StoryViewActivity.EXTRA_USER
                    , buildUserForNotification(contributionNotification.getNickname(), contributionNotification.getPicture()));

            context.getApplicationContext().startActivity(storyViewIntent);
        }

        @NonNull
        private Story buildStory() {
            Story story = new Story();
            story.setKey(contributionNotification.getStoryId());
            return story;
        }
    }

    private class OnChatNotificationSelectedListener implements OnNotificationSelectedListener {

        private ChatNotification chatNotification;

        public OnChatNotificationSelectedListener(ChatNotification chatNotification) {
            this.chatNotification = chatNotification;
        }

        @Override
        public void onNotificationSelected(Notification notification) {
            CountryProgramManager.switchToUserCountryProgram();

            Intent openChatIntent = new Intent(context.getApplicationContext(), ChatRoomActivity.class);
            openChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openChatIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM_KEY, chatNotification.getChatRoomId());
            context.getApplicationContext().startActivity(openChatIntent);
        }
    }
}
