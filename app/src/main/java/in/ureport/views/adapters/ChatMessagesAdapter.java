package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;
import java.util.Random;

import in.ureport.R;
import in.ureport.models.ChatMessage;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_OTHER = 1;

    private final int [] colors = {R.color.group_user_color1, R.color.group_user_color2};

    private List<ChatMessage> chatMessages;
    private User user;

    private DateFormat hourFormatter;

    public ChatMessagesAdapter(List<ChatMessage> chatMessages, User user) {
        this.chatMessages = chatMessages;
        this.user = user;
        hourFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
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

    public void setUser(User user) {
        this.user = user;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView message;
        private final TextView date;
        private final TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.message);
            date = (TextView) itemView.findViewById(R.id.date);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        private void bindView(ChatMessage chatMessage) {
            message.setText(chatMessage.getMessage());
            date.setText(hourFormatter.format(chatMessage.getDate()));
            bindName(chatMessage);
        }

        private void bindName(ChatMessage chatMessage) {
            if(chatMessage.getChatRoom() instanceof GroupChatRoom
            && getItemViewType() == TYPE_OTHER) {
                name.setVisibility(View.VISIBLE);
                name.setText(chatMessage.getUser().getUsername());
                name.setTextColor(itemView.getContext().getResources().getColor(colors[getRandomInt(0, colors.length-1)]));
            }
        }
    }

    private int getRandomInt(int min, int max){
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

}
