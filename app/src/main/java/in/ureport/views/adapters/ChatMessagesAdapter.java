package in.ureport.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.marcorei.infinitefire.InfiniteFireArray;
import com.marcorei.infinitefire.InfiniteFireRecyclerViewAdapter;
import com.marcorei.infinitefire.InfiniteFireSnapshot;

import in.ureport.fragments.RecordAudioFragment;
import in.ureport.models.ChatMessage;
import in.ureport.models.User;
import in.ureport.views.holders.ChatMessageViewHolder;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatMessagesAdapter extends InfiniteFireRecyclerViewAdapter<ChatMessage> {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_PICTURE = 1;
    public static final int TYPE_YOUTUBE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_AUDIO = 4;
    public static final int TYPE_FILE = 5;

    private RecordAudioFragment recordAudioFragment;
    private User user;

    private ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener;

    public ChatMessagesAdapter(User user, InfiniteFireArray snapshots) {
        super(snapshots, 0, 0);
        this.user = user;
        this.recordAudioFragment = new RecordAudioFragment();
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(parent.getContext(), parent, viewType);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = getChatMessage(position);

        ChatMessageViewHolder chatMessageViewHolder = ((ChatMessageViewHolder)holder);
        chatMessageViewHolder.setOnChatMessageSelectedListener(onChatMessageSelectedListener);
        chatMessageViewHolder.setRecordAudioFragment(recordAudioFragment);
        chatMessageViewHolder.bindView(user, chatMessage);
    }

    @NonNull
    public ChatMessage getChatMessage(int position) {
        InfiniteFireSnapshot<ChatMessage> infiniteFireArray = snapshots.getItem(position);
        ChatMessage chatMessage = infiniteFireArray.getValue();
        chatMessage.setKey(infiniteFireArray.getKey());
        return chatMessage;
    }

    @Override
    public long getItemId(int position) {
        return snapshots.getItem(position).getKey().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = snapshots.getItem(position).getValue();
        if(chatMessage != null && chatMessage.getMedia() != null) {
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

    public void setOnChatMessageSelectedListener(ChatMessageViewHolder.OnChatMessageSelectedListener onChatMessageSelectedListener) {
        this.onChatMessageSelectedListener = onChatMessageSelectedListener;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
