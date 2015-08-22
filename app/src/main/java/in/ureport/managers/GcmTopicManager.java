package in.ureport.managers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;

import java.io.IOException;
import java.util.Set;

import in.ureport.models.User;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class GcmTopicManager {

    private static final String TAG = "GcmTopicManager";

    public static final String TOPICS_PATH = "/topics/chats-";

    private Context context;

    public GcmTopicManager(Context context) {
        this.context = context;
    }

    public void registerUserTopics(String pushIdentity, User user) {
        try {
            Set<String> chatRooms = user.getChatRooms().keySet();

            for (String chatRoom : chatRooms) {
                GcmPubSub gcmPubSub = GcmPubSub.getInstance(context);
                gcmPubSub.subscribe(pushIdentity, TOPICS_PATH + chatRoom, null);
            }
        } catch (IOException exception) {
            Log.e(TAG, "registerUserTopics ", exception);
        }
    }
}
