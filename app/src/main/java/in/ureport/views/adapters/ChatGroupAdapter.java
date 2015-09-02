package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.models.GroupChatRoom;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GroupChatRoom> groupChats;

    private ChatGroupListener chatGroupListener;

    public ChatGroupAdapter() {
        this.groupChats = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(groupChats.get(position));
    }

    @Override
    public long getItemId(int position) {
        return groupChats.get(position).getKey().hashCode();
    }

    public void updateData(List<GroupChatRoom> groupChatRooms) {
        this.groupChats = groupChatRooms;
        notifyDataSetChanged();
    }

    public List<GroupChatRoom> getGroupChats() {
        return groupChats;
    }

    @Override
    public int getItemCount() {
        return groupChats.size();
    }

    public void updateGroupChatRoom(GroupChatRoom groupChatRoom) {
        int groupPosition = groupChats.indexOf(groupChatRoom);
        if(groupPosition >= 0) {
            groupChats.set(groupPosition, groupChatRoom);
            notifyItemChanged(groupPosition);
        }
    }

    public void removeGroupChatRoom(GroupChatRoom groupChatRoom) {
        groupChats.remove(groupChatRoom);
        notifyDataSetChanged();
    }

    public void addGroupChatRoom(GroupChatRoom groupChatRoom) {
        groupChats.add(groupChatRoom);
        notifyDataSetChanged();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);

            Button join = (Button) itemView.findViewById(R.id.join);
            join.setOnClickListener(onJoinClickListener);

            itemView.setOnClickListener(onItemClickListener);
        }

        private void bindView(GroupChatRoom groupChatRoom) {
            title.setText(groupChatRoom.getTitle());
            description.setText(groupChatRoom.getSubject());
        }

        private View.OnClickListener onJoinClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatGroupListener != null) {
                    chatGroupListener.onJoinChatGroup(groupChats.get(getLayoutPosition()));
                }
            }
        };

        private View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chatGroupListener != null)
                    chatGroupListener.onViewGroupInfo(groupChats.get(getLayoutPosition()));
            }
        };
    }

    public void setChatGroupListener(ChatGroupListener chatGroupListener) {
        this.chatGroupListener = chatGroupListener;
    }

    public interface ChatGroupListener {
        void onJoinChatGroup(GroupChatRoom groupChatRoom);
        void onViewGroupInfo(GroupChatRoom groupChatRoom);
    }
}
