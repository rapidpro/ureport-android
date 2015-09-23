package in.ureport.views.adapters;

import android.support.v4.util.Pair;
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
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;
import in.ureport.models.holders.ChatRoomHolder;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatRoomsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatRoomHolder> chatRooms;

    private DateFormat hourFormatter;

    private OnChatRoomSelectedListener onChatRoomSelectedListener;

    public ChatRoomsAdapter() {
        this.chatRooms = new ArrayList<>();

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
        ((ViewHolder)holder).bindView(chatRooms.get(position));
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
        chatRooms.remove(chatRoom);
        notifyDataSetChanged();
    }

    public void updateData(List<ChatRoomHolder> chatRooms) {
        this.chatRooms = chatRooms;
        notifyDataSetChanged();
    }

    public void updateChatRoom(ChatRoomHolder chatRoom) {
        int chatRoomPosition = chatRooms.indexOf(chatRoom);
        if(chatRoomPosition >= 0) {
            chatRooms.set(chatRoomPosition, chatRoom);
            notifyItemChanged(chatRoomPosition);
        }
    }

    public void addChatRoom(ChatRoomHolder chatRoom) {
        chatRooms.add(chatRoom);
        notifyDataSetChanged();
    }

    public List<ChatRoomHolder> getChatRooms() {
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
                name.setText(friend.getNickname());
                ImageLoader.loadPersonPictureToImageView(picture, friend.getPicture());
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
                if (onChatRoomSelectedListener != null) {
                    Pair<View, String> picturePair = Pair.create((View)picture
                            , itemView.getContext().getString(R.string.transition_profile_picture));
                    Pair<View, String> nicknamePair = Pair.create((View)name
                            , itemView.getContext().getString(R.string.transition_profile_nickname));

                    ChatRoomHolder chatRoomHolder = chatRooms.get(getLayoutPosition());
                    onChatRoomSelectedListener.onChatRoomSelected(chatRoomHolder.chatRoom, chatRoomHolder.members
                            , picturePair);//, nicknamePair);
                }
            }
        };
    }

    public void setOnChatRoomSelectedListener(OnChatRoomSelectedListener onChatRoomSelectedListener) {
        this.onChatRoomSelectedListener = onChatRoomSelectedListener;
    }

    public interface OnChatRoomSelectedListener {
        void onChatRoomSelected(ChatRoom chatRoom, ChatMembers chatMembers, Pair<View, String>... views);
    }
}
