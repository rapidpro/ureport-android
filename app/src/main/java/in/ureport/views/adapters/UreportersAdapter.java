package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.listener.ChatCreationListener;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class UreportersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> ureportersList;
    private Boolean selectionEnabled = false;

    private ChatCreationListener chatCreationListener;

    public UreportersAdapter(List<User> ureportersList) {
        this.ureportersList = ureportersList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_ureporter, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(ureportersList.get(position));
    }

    @Override
    public int getItemCount() {
        return ureportersList.size();
    }

    public void setChatCreationListener(ChatCreationListener chatCreationListener) {
        this.chatCreationListener = chatCreationListener;
    }

    public void setSelectionEnabled(Boolean selectionEnabled) {
        this.selectionEnabled = selectionEnabled;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView firstLetter;
        private final CheckBox selected;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            firstLetter = (TextView) itemView.findViewById(R.id.firstLetter);
            selected = (CheckBox) itemView.findViewById(R.id.selected);
            itemView.setOnClickListener(selectionEnabled ? null : onItemClickListener);
        }

        public void bindView(User user) {
            name.setText(user.getUsername());
            firstLetter.setText(user.getUsername().toUpperCase());
            selected.setVisibility(selectionEnabled ? View.VISIBLE : View.GONE);
        }

        private View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatCreationListener != null)
                    chatCreationListener.onCreateIndividualChatCalled();
            }
        };
    }
}
