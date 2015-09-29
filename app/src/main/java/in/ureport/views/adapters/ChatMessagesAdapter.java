package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.models.ChatMessage;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_OTHER = 1;

    private List<ChatMessage> chatMessages;
    private User user;

    private DateFormat hourFormatter;

    private OnChatMessageSelectedListener onChatMessageSelectedListener;

    public ChatMessagesAdapter(User user) {
        this.chatMessages = new ArrayList<>();
        this.user = user;
        hourFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case TYPE_USER:
                return new ViewHolder(inflater.inflate(R.layout.item_chat_message_me, parent, false));
            default:
            case TYPE_OTHER:
                return new ViewHolder(inflater.inflate(R.layout.item_chat_message_other, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(chatMessages.get(position));
    }

    @Override
    public long getItemId(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        return chatMessage.getKey() != null ? chatMessage.getKey().hashCode() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(user != null && chatMessages.get(position).getUser().equals(user)) {
            return TYPE_USER;
        }
        return TYPE_OTHER;
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

    public void setOnChatMessageSelectedListener(OnChatMessageSelectedListener onChatMessageSelectedListener) {
        this.onChatMessageSelectedListener = onChatMessageSelectedListener;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView message;
        private final TextView date;
        private final TextView name;
        private final ImageView media;

        public ViewHolder(View itemView) {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.message);
            date = (TextView) itemView.findViewById(R.id.date);
            name = (TextView) itemView.findViewById(R.id.name);
            media = (ImageView) itemView.findViewById(R.id.media);
            media.setOnClickListener(onMediaClickListener);
        }

        private void bindView(ChatMessage chatMessage) {
            bindMessage(chatMessage);
            date.setText(hourFormatter.format(chatMessage.getDate()));
            bindName(chatMessage);

            if(getItemViewType() == TYPE_OTHER) {
                itemView.setOnLongClickListener(null);
                media.setOnLongClickListener(null);
            } else {
                itemView.setOnLongClickListener(onLongClickListener);
                media.setOnLongClickListener(onLongClickListener);
            }
        }

        private void bindMessage(ChatMessage chatMessage) {
            if(chatMessage.getMessage() != null) {
                media.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText(chatMessage.getMessage());
            } else if(chatMessage.getMedia() != null) {
                message.setVisibility(View.GONE);
                media.setVisibility(View.VISIBLE);
                ImageLoader.loadGenericPictureToImageView(media, chatMessage.getMedia());
            }
        }

        private void bindName(ChatMessage chatMessage) {
            if(getItemViewType() == TYPE_OTHER) {
                name.setVisibility(View.VISIBLE);
                name.setText(chatMessage.getUser().getNickname());
            }
        }

        private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(onChatMessageSelectedListener != null) {
                    onChatMessageSelectedListener.onChatMessageSelected(chatMessages.get(getLayoutPosition()));
                }
                return false;
            }
        };

        private View.OnClickListener onMediaClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onChatMessageSelectedListener != null) {
                    onChatMessageSelectedListener.onMediaChatMessageView(chatMessages.get(getLayoutPosition()), (ImageView)view);
                }
            }
        };
    }

    public interface OnChatMessageSelectedListener {
        void onMediaChatMessageView(ChatMessage chatMessage, ImageView mediaImageView);
        void onChatMessageSelected(ChatMessage chatMessage);
    }
}
