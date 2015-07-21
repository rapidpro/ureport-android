package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import in.ureport.R;
import in.ureport.models.ChatGroup;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatRoomsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatRoom> chatRooms;

    private DateFormat hourFormatter;

    private OnChatRoomSelectedListener onChatRoomSelectedListener;

    public ChatRoomsAdapter(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
        hourFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
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
    public int getItemCount() {
        return chatRooms.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView firstLetter;
        private final TextView lastMessage;
        private final TextView lastMessageDate;
        private final TextView unreadMessages;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            firstLetter = (TextView) itemView.findViewById(R.id.firstLetter);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
            lastMessageDate = (TextView) itemView.findViewById(R.id.lastMessageDate);
            unreadMessages = (TextView) itemView.findViewById(R.id.unreadMessages);
            itemView.setOnClickListener(onItemClickListener);
        }

        private void bindView(ChatRoom chatRoom) {
            if(chatRoom instanceof IndividualChatRoom) {
                User friend = ((IndividualChatRoom)chatRoom).getFriend();
                name.setText("@"+friend.getUsername());
                firstLetter.setText(friend.getUsername().toUpperCase());
            } else if(chatRoom instanceof GroupChatRoom) {
                ChatGroup chatGroup = ((GroupChatRoom)chatRoom).getChatGroup();
                name.setText(chatGroup.getTitle());
                firstLetter.setText(chatGroup.getTitle());
            }

            if(chatRoom.getLastMessage() != null) {
                lastMessage.setVisibility(View.VISIBLE);
                lastMessage.setText(chatRoom.getLastMessage());

                unreadMessages.setVisibility(View.VISIBLE);
                unreadMessages.setText(String.valueOf(chatRoom.getUnreadMessages()));
            } else {
                lastMessage.setVisibility(View.GONE);
                unreadMessages.setVisibility(View.GONE);
            }

            lastMessageDate.setText(hourFormatter.format(chatRoom.getLastMessageDate()));
        }

        private View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onChatRoomSelectedListener != null)
                    onChatRoomSelectedListener.onChatRoomSelected(chatRooms.get(getLayoutPosition()));
            }
        };
    }

    public void setOnChatRoomSelectedListener(OnChatRoomSelectedListener onChatRoomSelectedListener) {
        this.onChatRoomSelectedListener = onChatRoomSelectedListener;
    }

    public interface OnChatRoomSelectedListener {
        void onChatRoomSelected(ChatRoom chatRoom);
    }
}
