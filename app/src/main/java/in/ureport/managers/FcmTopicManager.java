package in.ureport.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.network.FcmTopicServices;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class FcmTopicManager {

    private static final String TAG = "FcmTopicManager";

    public static final String CHAT_TOPICS_PATH = "chats-";
    public static final String STORY_TOPICS_PATH = "story-";

    private FcmTopicServices services;

    public FcmTopicManager(Context context) {
        this.services = new FcmTopicServices(context);
    }

    public void registerToChatRoomTopics(String pushIdentity, User user) {
        if (user != null && user.getChatRooms() != null && user.getChatRooms().size() > 0) {
            Set<String> chatRooms = user.getChatRooms().keySet();
            for (String chatRoom : chatRooms) {
                try {
                    services.registerOnTopic(pushIdentity,
                            getTopicName(CHAT_TOPICS_PATH, chatRoom)).execute();
                    Log.d(TAG, "registerToChatRoomTopics: registered on topic: " + getTopicName(CHAT_TOPICS_PATH, chatRoom));
                } catch(Exception exception) {
                    Log.e(TAG, "registerToChatRoomTopics: ", exception);
                }
            }
        }
    }

    public void registerToStoryTopic(final User user, final Story story) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                    if(isUserValid(user)
                    && story != null && !TextUtils.isEmpty(story.getKey())) {
                        try {
                            services.registerOnTopic(user.getPushIdentity(),
                                    getTopicName(STORY_TOPICS_PATH, story.getKey())).execute();
                            Log.d(TAG, "registerToStoryTopic: registered on topic: " + getTopicName(STORY_TOPICS_PATH, story.getKey()));
                        } catch (Exception exception) {
                            Log.e(TAG, "registerToStoryTopic: ", exception);
                        }
                    }
                return null;
            }
        }.execute();
    }

    public void registerToChatRoomTopic(final User user, final String chatRoomKey) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if(isUserValid(user) && !TextUtils.isEmpty(chatRoomKey)) {
                    try {
                        services.registerOnTopic(user.getPushIdentity(),
                                getTopicName(CHAT_TOPICS_PATH, chatRoomKey)).execute();
                        Log.d(TAG, "registerToChatRoomTopic: registered on topic: " + getTopicName(CHAT_TOPICS_PATH, chatRoomKey));
                    } catch(Exception exception) {
                        Log.e(TAG, "registerToChatRoomTopic ", exception);
                    }
                }
                return null;
            }
        }.execute();
    }

    public void unregisterToChatRoomTopic(final User user, final String chatRoomKey) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isUserValid(user) && !TextUtils.isEmpty(chatRoomKey)) {
                    try {
                        services.unregisterFromTopic(user.getPushIdentity(),
                                getTopicName(CHAT_TOPICS_PATH, chatRoomKey)).execute();
                        Log.d(TAG, "unregisterToChatRoomTopic: unregistered on topic: " + getTopicName(CHAT_TOPICS_PATH, chatRoomKey));
                    } catch (Exception exception) {
                        Log.e(TAG, "unregisterToChatRoomTopic ", exception);
                    }
                }
                return null;
            }
        }.execute();
    }

    private boolean isUserValid(User user) {
        return user != null && !TextUtils.isEmpty(user.getPushIdentity());
    }

    @NonNull
    private String getTopicName(String topic, String key) {
        return topic + key;
    }
}
