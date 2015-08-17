package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.DateFormatter;
import in.ureport.R;
import in.ureport.managers.ImageLoader;
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

    private OnContributionAddListener onContributionAddListener;

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
        return contributions.get(position).getKey().hashCode();
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

    public void addContribution(Contribution contribution) {
        if(contributions == null) {
            contributions = new ArrayList<>();
            contributions.add(contribution);
            notifyDataSetChanged();
        } else {
            contributions.add(contribution);
            notifyItemInserted(contributions.size());
        }
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
        notifyDataSetChanged();
    }

    private class AddItemViewHolder extends RecyclerView.ViewHolder {

        private final EditText description;

        public AddItemViewHolder(View itemView) {
            super(itemView);

            description = (EditText) itemView.findViewById(R.id.description);
            description.setOnEditorActionListener(onDescriptionEditorActionListener);

            Button addContribution = (Button) itemView.findViewById(R.id.addContribution);
            addContribution.setOnClickListener(onContributionClickListener);
        }

        private View.OnClickListener onContributionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewContribution();
            }
        };

        private void addNewContribution() {
            if(description.length() > 0) {
                if(onContributionAddListener != null) onContributionAddListener.onContributionAdd(description);
            }
        }

        private TextView.OnEditorActionListener onDescriptionEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                addNewContribution();
                return false;
            }
        };
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView contribution;
        private final TextView author;
        private final ImageView picture;
        private final TextView date;

        private final DateFormatter dateFormatter;

        public ItemViewHolder(View itemView) {
            super(itemView);

            dateFormatter = new DateFormatter();

            picture = (ImageView) itemView.findViewById(R.id.picture);
            contribution = (TextView) itemView.findViewById(R.id.contribution);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
        }

        private void bindView(Contribution contribution) {
            ImageLoader.loadPersonPictureToImageView(picture, contribution.getAuthor().getPicture());
            this.contribution.setText(contribution.getContent());
            this.author.setText(contribution.getAuthor().getNickname());

            String timeElapsed = dateFormatter.getTimeElapsed(contribution.getCreatedDate().getTime()
                    , itemView.getContext().getString(R.string.date_now));
            this.date.setText(timeElapsed.toLowerCase());
        }
    }

    public void setOnContributionAddListener(OnContributionAddListener onContributionAddListener) {
        this.onContributionAddListener = onContributionAddListener;
    }

    public interface OnContributionAddListener {
        void onContributionAdd(EditText contentText);
    }
}
