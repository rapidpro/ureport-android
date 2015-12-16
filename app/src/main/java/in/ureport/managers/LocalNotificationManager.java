package in.ureport.managers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;

import in.ureport.R;
import in.ureport.activities.MainActivity;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.db.ChatNotification;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class LocalNotificationManager {

    private static final String MESSAGE_FORMAT = "%1$s   %2$s";

    private Context context;

    public LocalNotificationManager(Context context) {
        this.context = context;
    }

    public enum Type {
        Chat(100, "ChatGroup"),
        StoryStatus(101, "StoryStatus"),
        Contribution(102),
        Message(103);

        public int id;
        public String group;

        Type(int id) {
            this.id = id;
        }

        Type(int id, String group) {
            this.id = id;
            this.group = group;
        }
    }

    public void cancelContributionNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(Type.Contribution.id);
    }

    public void sendContributionNotification(Contribution contribution, Story story) {
        Type type = Type.Contribution;

        String title = context.getString(R.string.ureport_contribution_notification_title);
        String content = contribution.getAuthor().getNickname() + ": " + contribution.getContent();

        NotificationCompat.Builder notificationBuilder = getDefaultNotificationBuilder(title
                , content, getContributionIntent(story));
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(type.id, notificationBuilder.build());
    }

    private PendingIntent getContributionIntent(Story story) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_CONTRIBUTION_NOTIFICATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.EXTRA_STORY, story);
        return PendingIntent.getActivity(context, MainActivity.REQUEST_CODE_CONTRIBUTION_NOTIFICATION
                , intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void cancelMessageNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(Type.Message.id);
    }

    public void sendMessageNotification(String message) {
        Type type = Type.Message;

        String title = context.getString(R.string.ureport_message_title);
        NotificationCompat.Builder notificationBuilder = getDefaultNotificationBuilder(title, message, getMessageIntent());
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(type.id, notificationBuilder.build());
    }

    private PendingIntent getMessageIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_OPEN_MESSAGE_NOTIFICATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, MainActivity.REQUEST_CODE_MESSAGE_NOTIFICATION, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void cancelChatNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(Type.Chat.id);
    }

    public void sendChatListNotification(List<ChatNotification> chatNotificationList) {
        Type type = LocalNotificationManager.Type.Chat;
        ChatNotification lastNotification = chatNotificationList.get(0);

        String summaryText = context.getResources().getQuantityString(R.plurals.title_new_message, chatNotificationList.size());
        summaryText = String.format(summaryText, chatNotificationList.size());

        String notificationLine = String.format(MESSAGE_FORMAT, lastNotification.getNickname()
                , lastNotification.getMessage());

        NotificationCompat.Builder notificationBuilder = getDefaultNotificationBuilder(summaryText, notificationLine, getChatPendingIntent());

        NotificationCompat.InboxStyle notificationInboxStyle = buildInboxStyle(chatNotificationList, summaryText);
        notificationBuilder.setStyle(notificationInboxStyle)
            .setGroup(type.group)
            .setGroupSummary(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(type.id, notificationBuilder.build());
    }

    private NotificationCompat.Builder getDefaultNotificationBuilder(String title, String message, PendingIntent pendingIntent) {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_notification);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentText(message)
                .setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri);
    }

    private PendingIntent getChatPendingIntent() {
        Intent chatIntent = new Intent(context, MainActivity.class);
        chatIntent.setAction(MainActivity.ACTION_OPEN_CHAT_NOTIFICATION);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, MainActivity.REQUEST_CODE_CHAT_NOTIFICATION, chatIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private NotificationCompat.InboxStyle buildInboxStyle(List<ChatNotification> chatNotificationList
            , String summaryText) {
        NotificationCompat.InboxStyle notificationInboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(summaryText)
                .setSummaryText(context.getString(R.string.summary_notification));

        for (ChatNotification notification : chatNotificationList) {
            String notificationLine = String.format(MESSAGE_FORMAT, notification.getNickname()
                    , notification.getMessage());
            notificationInboxStyle.addLine(notificationLine);
        }
        return notificationInboxStyle;
    }

}
