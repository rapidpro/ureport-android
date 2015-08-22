package in.ureport.network;

import android.content.Context;
import android.util.Log;

import in.ureport.BuildConfig;
import in.ureport.R;
import in.ureport.managers.GcmTopicManager;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import retrofit.RestAdapter;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class GcmServices {

    private static final String ENDPOINT = "https://gcm-http.googleapis.com";

    private final GcmApi gcmApi;
    private Context context;

    public GcmServices(Context context) {
        this.context = context;

        RestAdapter restAdapter = buildRestAdapter();
        if(BuildConfig.DEBUG)
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        gcmApi = restAdapter.create(GcmApi.class);
    }

    public GcmApi.Response sendChatMessage(ChatRoom chatRoom, ChatMessage chatMessage) {
        ChatMessageHolder chatMessageHolder = new ChatMessageHolder();
        chatMessageHolder.chatRoom = chatRoom;
        chatMessageHolder.chatMessage = chatMessage;

        GcmApi.Input<ChatMessageHolder> chatMessageHolderInput = new GcmApi.Input<>(
                GcmTopicManager.TOPICS_PATH + chatRoom.getKey()
                , chatMessageHolder);

        return gcmApi.sendChatMessage(context.getString(R.string.gcm_api_key), chatMessageHolderInput);
    }

    private RestAdapter buildRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
    }

    public class ChatMessageHolder {
        ChatRoom chatRoom;
        ChatMessage chatMessage;
    }
}
