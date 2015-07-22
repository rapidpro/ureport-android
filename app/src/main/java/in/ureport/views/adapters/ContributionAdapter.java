package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.managers.UserDataManager;
import in.ureport.models.Contribution;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class ContributionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ITEM_ADD = 1;

    private static final long ADD_MEDIA_ITEM_ID = 1000;

    private List<Contribution> contributions;
    private User user;

    public ContributionAdapter(User user) {
        this.user = user;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case TYPE_ITEM_ADD:
                return new AddItemViewHolder(inflater.inflate(R.layout.item_add_contribution, parent, false));
            default:
                return new ItemViewHolder(inflater.inflate(R.layout.item_contribution, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_ITEM) {
            ((ItemViewHolder)holder).bindView(contributions.get(position));
        }
    }

    @Override
    public long getItemId(int position) {
        if(getItemViewType(position) == TYPE_ITEM_ADD) {
            return ADD_MEDIA_ITEM_ID;
        }
        String id = contributions.get(position).hashCode() + "" + position;
        return id.hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == contributions.size()) {
            return TYPE_ITEM_ADD;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if(contributions == null) return 0;

        return contributions.size() + 1;
    }

    public void startContribution() {
        contributions = new ArrayList<>();
        notifyDataSetChanged();
    }

    private void addContribution(Contribution contribution) {
        contributions.add(contribution);
        notifyDataSetChanged();
    }

    private class AddItemViewHolder extends RecyclerView.ViewHolder {

        private EditText name;

        public AddItemViewHolder(View itemView) {
            super(itemView);

            name = (EditText) itemView.findViewById(R.id.name);

            Button addContribution = (Button) itemView.findViewById(R.id.addContribution);
            addContribution.setOnClickListener(onContributionClickListener);
        }

        private View.OnClickListener onContributionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameText = name.getText().toString();
                if(name.length() > 0) {
                    Contribution contribution = new Contribution(nameText, user);
                    addContribution(contribution);
                    name.setText("");
                }
            }
        };
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView contribution;
        private final TextView author;
        private final ImageView picture;

        public ItemViewHolder(View itemView) {
            super(itemView);

            picture = (ImageView) itemView.findViewById(R.id.picture);
            contribution = (TextView) itemView.findViewById(R.id.contribution);
            author = (TextView) itemView.findViewById(R.id.author);
        }

        private void bindView(Contribution contribution) {
            this.picture.setImageResource(UserDataManager.getUserImage(itemView.getContext(), user));
            this.contribution.setText(contribution.getContribution());
            this.author.setText("@"+user.getUsername());

        }
    }
}
