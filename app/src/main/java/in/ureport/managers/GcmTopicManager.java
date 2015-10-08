package in.ureport.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;

import java.util.Set;

import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class GcmTopicManager {

    private static final String TAG = "GcmTopicManager";

    public static final String CHAT_TOPICS_PATH = "/topics/chats-";
    public static final String STORY_TOPICS_PATH = "/topics/story-";

    private Context context;

    public GcmTopicManager(Context context) {
        this.context = context;
    }

    public void registerToChatRoomTopics(String pushIdentity, User user) {
        try {
            Set<String> chatRooms = user.getChatRooms().keySet();

            for (String chatRoom : chatRooms) {
                GcmPubSub gcmPubSub = GcmPubSub.getInstance(context);
                gcmPubSub.subscribe(pushIdentity, getTopicName(CHAT_TOPICS_PATH, chatRoom), null);
            }
        } catch (Exception exception) {
            Log.e(TAG, "registerToChatRoomTopics ", exception);
        }
    }

    public void registerToStoryTopic(final User user, final Story story) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(user.getPushIdentity() != null) {
                        GcmPubSub gcmPubStub = GcmPubSub.getInstance(context);
                        gcmPubStub.subscribe(user.getPushIdentity(), getTopicName(STORY_TOPICS_PATH, story.getKey()), null);
                    }
                } catch(Exception exception) {
                    Log.e(TAG, "registerToChatRoomTopic ", exception);
                }
                return null;
            }
        }.execute();
    }

    public void registerToChatRoomTopic(final User user, final String chatRoomKey) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(user.getPushIdentity() != null) {
                        GcmPubSub gcmPubStub = GcmPubSub.getInstance(context);
                        gcmPubStub.subscribe(user.getPushIdentity(), getTopicName(CHAT_TOPICS_PATH, chatRoomKey), null);
                    }
                } catch(Exception exception) {
                    Log.e(TAG, "registerToChatRoomTopic ", exception);
                }
                return null;
            }
        }.execute();
    }

    public void unregisterToChatRoomTopic(final User user, final String chatRoomKey) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (user.getPushIdentity() != null) {
                        GcmPubSub gcmPubStub = GcmPubSub.getInstance(context);
                        gcmPubStub.unsubscribe(user.getPushIdentity(), getTopicName(CHAT_TOPICS_PATH, chatRoomKey));
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "unregisterToChatRoomTopic ", exception);
                }
                return null;
            }
        }.execute();
    }

    @NonNull
    private String getTopicName(String topic, String key) {
        return topic + key;
    }
}
