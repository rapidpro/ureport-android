package in.ureport.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import in.ureport.BuildConfig;
import in.ureport.R;
import in.ureport.managers.GcmTopicManager;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class GcmServices {

    private static final String ENDPOINT = "https://gcm-http.googleapis.com";
    public static final String DATE_STYLE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

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
                GcmTopicManager.CHAT_TOPICS_PATH + chatRoom.getKey(), chatMessageHolder);

        return gcmApi.sendChatMessage(context.getString(R.string.gcm_api_key), chatMessageHolderInput);
    }

    private RestAdapter buildRestAdapter() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setDateFormat(DATE_STYLE).create();

        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    public class ChatMessageHolder {
        @Expose
        ChatRoom chatRoom;
        @Expose
        ChatMessage chatMessage;
    }
}
