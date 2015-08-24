package in.ureport.managers;

import android.app.Notification;
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
import in.ureport.activities.ChatActivity;
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
        Chat(100, "ChatGroup");

        public int id;
        public String group;

        Type(int id, String group) {
            this.id = id;
            this.group = group;
        }
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

        NotificationCompat.InboxStyle notificationInboxStyle = buildInboxStyle(chatNotificationList, summaryText);

        String notificationLine = String.format(MESSAGE_FORMAT, lastNotification.getNickname()
                , lastNotification.getMessage());
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon);

        PendingIntent pendingIntent = getPendingIntent();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification summaryNotification = new NotificationCompat.Builder(context)
                .setContentTitle(summaryText)
                .setContentText(notificationLine)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.icon)
                .setLargeIcon(largeIcon)
                .setStyle(notificationInboxStyle)
                .setGroup(type.group)
                .setGroupSummary(true)
                .setSound(soundUri)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(type.id, summaryNotification);
    }

    private PendingIntent getPendingIntent() {
        Intent chatIntent = new Intent(context, ChatActivity.class);
        return PendingIntent.getActivity(context, ChatActivity.REQUEST_CODE_CHAT_NOTIFICATION, chatIntent, PendingIntent.FLAG_CANCEL_CURRENT);
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
