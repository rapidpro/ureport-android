package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.ureport.R;
import in.ureport.listener.OnCreateIndividualChatListener;
import in.ureport.managers.ImageLoader;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class UreportersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "UreportersAdapter";

    private List<User> ureportersList;

    private Set<User> selectedUreporters;
    private Boolean selectionEnabled = false;
    private Integer maxSelectionCount;

    private OnCreateIndividualChatListener onCreateIndividualChatListener;

    public UreportersAdapter(List<User> ureportersList) {
        this.ureportersList = ureportersList;
    }

    public UreportersAdapter() {
        this.ureportersList = new ArrayList<>();
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
    public long getItemId(int position) {
        return ureportersList.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return ureportersList.size();
    }

    public void update(List<User> ureportersList) {
        this.ureportersList = ureportersList != null ? ureportersList : new ArrayList<User>();
        notifyDataSetChanged();
    }

    public void setOnCreateIndividualChatListener(OnCreateIndividualChatListener onCreateIndividualChatListener) {
        this.onCreateIndividualChatListener = onCreateIndividualChatListener;
    }

    public void setSelectionEnabled(Boolean selectionEnabled, Integer maxSelectionCount, List<User> users) {
        this.maxSelectionCount = maxSelectionCount;
        this.selectionEnabled = selectionEnabled;

        if(users != null)
            this.selectedUreporters = new HashSet<>(users);
        else
            this.selectedUreporters = new HashSet<>();
    }

    public void setSelectionEnabled(Boolean selectionEnabled, Integer maxSelectionCount) {
        setSelectionEnabled(selectionEnabled, maxSelectionCount, null);
    }

    public Set<User> getSelectedUreporters() {
        return selectedUreporters;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView picture;
        private final CheckBox selected;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            picture = (ImageView) itemView.findViewById(R.id.picture);
            selected = (CheckBox) itemView.findViewById(R.id.selected);
            itemView.setOnClickListener(selectionEnabled ? null : onItemClickListener);
            selected.setOnCheckedChangeListener(selectionEnabled ? onUserCheckedListener : null);
        }

        public void bindView(User user) {
            name.setText(user.getNickname());
            ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());

            selected.setVisibility(selectionEnabled ? View.VISIBLE : View.GONE);
            if(selectionEnabled) selected.setChecked(selectedUreporters.contains(user));

        }

        private View.OnClickListener onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onCreateIndividualChatListener != null)
                    onCreateIndividualChatListener.onCreateIndividualChat(ureportersList.get(getLayoutPosition()));
            }
        };

        private CompoundButton.OnCheckedChangeListener onUserCheckedListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(ureportersList.size() <= maxSelectionCount)
                        selectedUreporters.add(ureportersList.get(getLayoutPosition()));
                    else
                        showMaximumNumberLimitError();
                } else {
                    selectedUreporters.remove(ureportersList.get(getLayoutPosition()));
                }
            }
        };

        private void showMaximumNumberLimitError() {
            Toast.makeText(itemView.getContext()
                    , itemView.getContext().getString(R.string.ureporters_selected_maximum, maxSelectionCount)
                    , Toast.LENGTH_LONG).show();
        }
    }
}
