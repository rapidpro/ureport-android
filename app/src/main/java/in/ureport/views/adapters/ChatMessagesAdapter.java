package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.ureport.fragments.RecordAudioFragment;
import in.ureport.models.ChatMessage;
import in.ureport.models.User;
import in.ureport.views.holders.ChatMessageViewHolder;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_PICTURE = 1;
    public static final int TYPE_YOUTUBE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_AUDIO = 4;
    public static final int TYPE_FILE = 5;

    private RecordAudioFragment recordAudioFragment;
    private List<ChatMessage> chatMessages;
    private User user;

    private ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener;

    public ChatMessagesAdapter(User user) {
        this.chatMessages = new ArrayList<>();
        this.user = user;
        this.recordAudioFragment = new RecordAudioFragment();
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(parent.getContext(), parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessageViewHolder chatMessageViewHolder = ((ChatMessageViewHolder)holder);
        chatMessageViewHolder.setOnChatMessageSelectedListener(onChatMessageSelectedListener);
        chatMessageViewHolder.setRecordAudioFragment(recordAudioFragment);
        chatMessageViewHolder.bindView(user, chatMessages.get(position));
    }

    @Override
    public long getItemId(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        return chatMessage.getKey() != null ? chatMessage.getKey().hashCode() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if(chatMessage.getMedia() != null) {
            switch(chatMessage.getMedia().getType()) {
                case Picture:
                    return TYPE_PICTURE;
                case Video:
                    return TYPE_YOUTUBE;
                case VideoPhone:
                    return TYPE_VIDEO;
                case Audio:
                    return TYPE_AUDIO;
                case File:
                    return TYPE_FILE;
            }
        }
        return TYPE_TEXT;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void addChatMessage(ChatMessage chatMessage) {
        chatMessages.add(0, chatMessage);
        notifyItemInserted(0);
    }

    public void removeChatMessage(ChatMessage chatMessage) {
        int indexOfMessage = chatMessages.indexOf(chatMessage);
        if(indexOfMessage >= 0) {
            chatMessages.remove(indexOfMessage);
            notifyItemRemoved(indexOfMessage);
        }
    }

    public void setOnChatMessageSelectedListener(ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener) {
        this.onChatMessageSelectedListener = onChatMessageSelectedListener;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
