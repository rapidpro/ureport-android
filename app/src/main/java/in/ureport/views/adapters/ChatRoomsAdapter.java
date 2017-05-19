package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.CountryProgram;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;
import in.ureport.models.holders.ChatRoomHolder;
import io.rapidpro.sdk.FcmClient;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatRoomsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ChatRoomsAdapter";

    private static final int VIEW_TYPE_RAPIDPRO = 0;
    private static final int VIEW_TYPE_CHAT = 1;

    private List<ChatRoomHolder> chatRooms;
    private DateFormat hourFormatter;
    private OnChatRoomSelectedListener onChatRoomSelectedListener;

    private ChatRoomHolder selectedItem;
    private boolean forceClick = false;
    private boolean selectable = false;

    public ChatRoomsAdapter() {
        setHasStableIds(true);
        this.chatRooms = new ArrayList<>();
        hourFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_chat_room, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_RAPIDPRO:
                bindCountryProgram((ViewHolder) holder);
                break;
            case VIEW_TYPE_CHAT:
                bindChatRoom(holder, position);
        }
    }

    private void bindCountryProgram(ViewHolder viewHolder) {
        CountryProgram countryProgram = CountryProgramManager.getCurrentCountryProgram();

        viewHolder.name.setText(viewHolder.itemView.getContext().getString(R.string.ureport_name, countryProgram.getName()));
        viewHolder.name.setVisibility(View.VISIBLE);
        viewHolder.lastMessageText.setText(R.string.ureport_description);
        viewHolder.lastMessageText.setVisibility(View.VISIBLE);
        viewHolder.picture.setImageResource(R.mipmap.icon);
        viewHolder.bindUnreadMessages(FcmClient.getUnreadMessages());
    }

    private void bindChatRoom(RecyclerView.ViewHolder holder, int position) {
        try {
            ChatRoomHolder chatRoomHolder = getItem(position);

            holder.itemView.setSelected(isSelected(chatRoomHolder));
            ((ViewHolder) holder).bindView(chatRoomHolder);

            if(forceClick && isSelected(chatRoomHolder)) {
                forceClick = false;
                holder.itemView.performClick();
            }
        } catch(Exception exception) {
            Log.e(TAG, "onBindViewHolder: " + exception.getLocalizedMessage());
        }
    }

    private boolean isSelected(ChatRoomHolder chatRoomHolder) {
        return selectedItem != null && selectedItem.equals(chatRoomHolder);
    }

    @Override
    public long getItemId(int position) {
        return getItemViewType(position) == VIEW_TYPE_RAPIDPRO ? Integer.MAX_VALUE : getChatRoomId(getItem(position));
    }

    private int getChatRoomId(ChatRoomHolder holder) {
        return holder.chatRoom.getKey().hashCode();
    }

    @Override
    public int getItemCount() {
        return chatRooms.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_RAPIDPRO;
        }
        return VIEW_TYPE_CHAT;
    }

    public void removeChatRoom(ChatRoomHolder chatRoom) {
        for (int position = 0; position < chatRooms.size(); position++) {
            ChatRoomHolder chatRoomHolder = getItem(position);
            if(chatRoomHolder.chatRoom.equals(chatRoom.chatRoom)) {
                chatRooms.remove(position);
                break;
            }
        }
    }

    public void addAll(List<ChatRoomHolder> chatRooms) {
        this.chatRooms.addAll(chatRooms);
    }

    public int addChatRoom(ChatRoomHolder newChatRoomHolder) {
        int indexOfChatRoom = getIndexOfChatRoom(newChatRoomHolder);
        if(indexOfChatRoom < 0) {
            chatRooms.add(newChatRoomHolder);
            return chatRooms.size() - 1;
        } else {
            chatRooms.set(indexOfChatRoom, newChatRoomHolder);
            return indexOfChatRoom;
        }
    }

    public void fillSelectableWhenNull() {
        if(selectable && (selectedItem == null || getIndexOfChatRoom(selectedItem) < 0)) {
            selectFirst();
        }
    }

    public List<ChatRoomHolder> getChatRooms() {
        return chatRooms;
    }

    public void sortChatRooms() {
        Collections.sort(chatRooms, (item1, item2) -> item1.lastMessage != null && item2.lastMessage != null
                ? item2.lastMessage.getDate().compareTo(item1.lastMessage.getDate()) : 1);
        notifyDataSetChanged();
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
            bindUnreadMessages(chatRoom.getUnreadMessages());
        }

        private void bindUnreadMessages(Integer unreadMessagesCount) {
            if(unreadMessagesCount != null && unreadMessagesCount > 0) {
                unreadMessages.setVisibility(View.VISIBLE);
                unreadMessages.setText(String.valueOf(unreadMessagesCount));
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
                if (ChatRoomsAdapter.this.getItemViewType(getLayoutPosition()) == VIEW_TYPE_CHAT) {
                    ChatRoomHolder chatRoomHolder = getItem(getLayoutPosition());
                    onChatRoomSelectedListener.onChatRoomSelected(chatRoomHolder.chatRoom, chatRoomHolder.members);

                    selectCurrentPosition();
                } else {
                    unreadMessages.setText(null);
                    unreadMessages.setVisibility(View.GONE);
                    onChatRoomSelectedListener.onRapidproChatSelected();
                }
            }
        };

        private void selectCurrentPosition() {
            if(selectable) {
                if(selectedItem != null)
                    notifyItemChanged(getIndexOfChatRoom(selectedItem));
                selectedItem = getItem(getChatPosition(getLayoutPosition()));
                notifyItemChanged(getChatPosition(getLayoutPosition()));
            }
        }

    }

    private ChatRoomHolder getItem(int chatPosition) {
        return chatRooms.get(chatPosition - 1);
    }

    private int getChatPosition(int listPosition) {
        return listPosition - 1;
    }

    public void setOnChatRoomSelectedListener(OnChatRoomSelectedListener onChatRoomSelectedListener) {
        this.onChatRoomSelectedListener = onChatRoomSelectedListener;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        this.selectedItem = !selectable ? null : selectedItem;
    }

    public void selectFirst() {
        this.forceClick = true;
        this.selectedItem = getItem(0);
        this.notifyDataSetChanged();
    }

    public void selectChatRoom(ChatRoom chatRoom) {
        ChatRoomHolder holder = new ChatRoomHolder(chatRoom);
        int indexOfChat = getIndexOfChatRoom(holder);

        if(indexOfChat >= 0) {
            selectedItem = holder;
            notifyItemChanged(indexOfChat);
        }
    }

    private int getIndexOfChatRoom(ChatRoomHolder holder) {
        return chatRooms.indexOf(holder);
//        int indexOfChat = -1;
//
//        for (int position = 0; position < getItemCount(); position++) {
//            long itemId = getItemId(position);
//            if (getChatRoomId(holder) == itemId) {
//                indexOfChat = position;
//                break;
//            }
//        }
//        return indexOfChat;
    }

    public interface OnChatRoomSelectedListener {
        void onRapidproChatSelected();
        void onChatRoomSelected(ChatRoom chatRoom, ChatMembers chatMembers);
    }

}
