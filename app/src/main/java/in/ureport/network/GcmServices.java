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
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.holders.ContributionHolder;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class GcmServices {

    private static final String ENDPOINT = "https://gcm-http.googleapis.com";
    public static final String DATE_STYLE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String GCM_AUTHORIZATION = "key=%1$s";

    private final GcmApi gcmApi;
    private final String gcmKey;
    private Context context;

    public GcmServices(Context context) {
        this.context = context;
        this.gcmKey = String.format(GCM_AUTHORIZATION, context.getString(R.string.gcm_api_key));

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
        chatMessageHolderInput.setNotification(new GcmApi.Notification(context.getString(R.string.title_chat_message)
                , String.format("%1$s: %2$s", chatMessage.getUser().getNickname(), getChatMessage(chatMessage))));

        return gcmApi.sendData(gcmKey, chatMessageHolderInput);
    }

    private String getChatMessage(ChatMessage chatMessage) {
        return chatMessage.getMessage() != null && !chatMessage.getMessage().isEmpty() ? chatMessage.getMessage() : context.getString(R.string.prompt_media_notification);
    }

    public GcmApi.Response sendContribution(Story story, Contribution contribution) {
        ContributionHolder contributionHolder = new ContributionHolder(story, contribution);

        GcmApi.Input<ContributionHolder> contributionData = new GcmApi.Input<>(
                GcmTopicManager.STORY_TOPICS_PATH + story.getKey(), contributionHolder);

        return gcmApi.sendData(gcmKey, contributionData);
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
