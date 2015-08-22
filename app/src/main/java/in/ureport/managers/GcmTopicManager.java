package in.ureport.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
                gcmPubSub.subscribe(pushIdentity, getChatRoomTopic(chatRoom), null);
            }
        } catch (Exception exception) {
            Log.e(TAG, "registerUserTopics ", exception);
        }
    }

    @NonNull
    private String getChatRoomTopic(String chatRoom) {
        return TOPICS_PATH + chatRoom;
    }

    public void registerUserTopic(final User user, final String chatRoomKey) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(user.getPushIdentity() != null) {
                        GcmPubSub gcmPubStub = GcmPubSub.getInstance(context);
                        gcmPubStub.subscribe(user.getPushIdentity(), getChatRoomTopic(chatRoomKey), null);
                    }
                } catch(Exception exception) {
                    Log.e(TAG, "registerUserTopic ", exception);
                }
                return null;
            }
        }.execute();
    }

    public void unregisterUserTopic(final User user, final String chatRoomKey) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (user.getPushIdentity() != null) {
                        GcmPubSub gcmPubStub = GcmPubSub.getInstance(context);
                        gcmPubStub.unsubscribe(user.getPushIdentity(), getChatRoomTopic(chatRoomKey));
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "unregisterUserTopic ", exception);
                }
                return null;
            }
        }.execute();
    }
}
