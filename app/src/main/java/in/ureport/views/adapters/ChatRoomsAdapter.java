package in.ureport.views.adapters;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;
import in.ureport.models.holders.ChatRoomHolder;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatRoomsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ChatRoomsAdapter";

    private SortedList<ChatRoomHolder> chatRooms;
    private DateFormat hourFormatter;
    private OnChatRoomSelectedListener onChatRoomSelectedListener;

    private int selectedPosition = -1;
    private boolean selectable = false;

    public ChatRoomsAdapter() {
        this.chatRooms = new SortedList<>(ChatRoomHolder.class, sortedListAdapterCallback);

        hourFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_chat_room, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            holder.itemView.setSelected(selectedPosition == position);
            ((ViewHolder) holder).bindView(chatRooms.get(position));
        } catch(Exception exception) {
            Log.e(TAG, "onBindViewHolder: " + exception.getLocalizedMessage());
        }
    }

    @Override
    public long getItemId(int position) {
        return chatRooms.get(position).chatRoom.getKey().hashCode();
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public void removeChatRoom(ChatRoomHolder chatRoom) {
        for (int position = 0; position < chatRooms.size(); position++) {
            ChatRoomHolder chatRoomHolder = chatRooms.get(position);
            if(chatRoomHolder.chatRoom.equals(chatRoom.chatRoom)) {
                chatRooms.removeItemAt(position);
                break;
            }
        }
    }

    public void addAll(List<ChatRoomHolder> chatRooms) {
        this.chatRooms.addAll(chatRooms);
    }

    public void addChatRoom(ChatRoomHolder chatRoom) {
        chatRooms.add(chatRoom);
    }

    public void updateChatRoomMessage(ChatRoom chatRoom, ChatMessage chatMessage) {
        for (int position = 0; position < chatRooms.size(); position++) {
            ChatRoomHolder chatRoomHolder = chatRooms.get(position);
            if(chatRoomHolder.chatRoom.equals(chatRoom)) {
                chatRoomHolder.lastMessage = chatMessage;
                chatRooms.updateItemAt(position, chatRoomHolder);
                break;
            }
        }
    }

    public SortedList<ChatRoomHolder> getChatRooms() {
        return chatRooms;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView picture;
        private final TextView name;
        private final TextView lastMessageText;
        private final TextView lastMessageDate;
        private final TextView unreadMessages;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            picture = (ImageView) itemView.findViewById(R.id.picture);
            lastMessageText = (TextView) itemView.findViewById(R.id.lastMessage);
            lastMessageDate = (TextView) itemView.findViewById(R.id.lastMessageDate);
            unreadMessages = (TextView) itemView.findViewById(R.id.unreadMessages);
            itemView.setOnClickListener(onItemClickListener);
        }

        private void bindView(ChatRoomHolder chatRoomHolder) {
            ChatRoom chatRoom = chatRoomHolder.chatRoom;

            if(chatRoom instanceof IndividualChatRoom) {
                User friend = getFriend(chatRoomHolder);
                if(friend != null) {
                    name.setText(friend.getNickname());
                    ImageLoader.loadPersonPictureToImageView(picture, friend.getPicture());
                }
            } else if(chatRoom instanceof GroupChatRoom) {
                GroupChatRoom chatGroup = ((GroupChatRoom)chatRoom);
                name.setText(chatGroup.getTitle());
                ImageLoader.loadGroupPictureToImageView(picture, chatGroup.getPicture());
            }

            bindLastMessage(chatRoomHolder);
            bindUnreadMessages(chatRoom);
        }

        private void bindUnreadMessages(ChatRoom chatRoom) {
            if(chatRoom.getUnreadMessages() != null && chatRoom.getUnreadMessages() > 0) {
                unreadMessages.setVisibility(View.VISIBLE);
                unreadMessages.setText(String.valueOf(chatRoom.getUnreadMessages()));
            } else {
                unreadMessages.setVisibility(View.GONE);
            }
        }

        private void bindLastMessage(ChatRoomHolder chatRoomHolder) {
            if(chatRoomHolder.lastMessage != null) {
                lastMessageText.setVisibility(View.VISIBLE);
                lastMessageText.setText(chatRoomHolder.lastMessage.getMessage());

                lastMessageDate.setVisibility(View.VISIBLE);
                lastMessageDate.setText(hourFormatter.format(chatRoomHolder.lastMessage.getDate()));
            } else {
                lastMessageText.setVisibility(View.GONE);
                lastMessageDate.setVisibility(View.GONE);
            }
        }

        private User getFriend(ChatRoomHolder chatRoomHolder) {
            for (User user : chatRoomHolder.members.getUsers()) {
                if(!user.getKey().equals(UserManager.getUserId())) {
                    return user;
                }
            }
            return null;
        }

        private View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatRoomHolder chatRoomHolder = chatRooms.get(getLayoutPosition());
                onChatRoomSelectedListener.onChatRoomSelected(chatRoomHolder.chatRoom, chatRoomHolder.members);

                selectCurrentPosition();
            }
        };

        private void selectCurrentPosition() {
            if(selectable) {
                notifyItemChanged(selectedPosition);
                selectedPosition = getLayoutPosition();
                notifyItemChanged(selectedPosition);
            }
        }
    }

    public void setOnChatRoomSelectedListener(OnChatRoomSelectedListener onChatRoomSelectedListener) {
        this.onChatRoomSelectedListener = onChatRoomSelectedListener;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        this.selectedPosition = !selectable ? -1 : selectedPosition;
    }

    public interface OnChatRoomSelectedListener {
        void onChatRoomSelected(ChatRoom chatRoom, ChatMembers chatMembers);
    }

    private SortedListAdapterCallback<ChatRoomHolder> sortedListAdapterCallback = new SortedListAdapterCallback<ChatRoomHolder>(this) {
        @Override
        public int compare(ChatRoomHolder item1, ChatRoomHolder item2) {
            return item1.lastMessage != null && item2.lastMessage != null
                    ? item2.lastMessage.getDate().compareTo(item1.lastMessage.getDate()) : 1;
        }

        @Override
        public boolean areContentsTheSame(ChatRoomHolder oldItem, ChatRoomHolder newItem) {
            return oldItem.chatRoom.getKey().equals(newItem.chatRoom.getKey());
        }

        @Override
        public boolean areItemsTheSame(ChatRoomHolder item1, ChatRoomHolder item2) {
            return item1.chatRoom.getKey().equals(item2.chatRoom.getKey());
        }
    };
}
