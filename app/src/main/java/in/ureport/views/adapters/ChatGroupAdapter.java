package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.models.ChatGroup;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatGroup> chatGroups;

    private ChatGroupListener chatGroupListener;

    public ChatGroupAdapter(List<ChatGroup> chatGroups) {
        this.chatGroups = chatGroups;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(chatGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return chatGroups.size();
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
        }

        private void bindView(ChatGroup chatGroup) {
            title.setText(chatGroup.getTitle());
            description.setText(chatGroup.getDescription());
        }

        private View.OnClickListener onJoinClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatGroupListener != null) {
                    chatGroupListener.onJoinChatGroup(chatGroups.get(getLayoutPosition()));
                }
            }
        };
    }

    public void setChatGroupListener(ChatGroupListener chatGroupListener) {
        this.chatGroupListener = chatGroupListener;
    }

    public interface ChatGroupListener {
        void onJoinChatGroup(ChatGroup chatGroup);
    }
}
