package in.ureport.views.adapters;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.DateFormatter;
import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.managers.UserManager;
import in.ureport.models.Contribution;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class ContributionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Contribution> contributions;

    private OnContributionRemoveListener onContributionRemoveListener;
    private OnContributionDenounceListener onContributionDenounceListener;

    public ContributionAdapter() {
        contributions = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ItemViewHolder(inflater.inflate(R.layout.item_contribution, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).bindView(contributions.get(position));
    }

    @Override
    public long getItemId(int position) {
        return contributions.get(position).getKey().hashCode();
    }

    @Override
    public int getItemCount() {
        if (contributions == null) return 0;
        return contributions.size();
    }

    public void addContribution(Contribution contribution) {
        contributions.add(contribution);
        notifyItemInserted(contributions.size());
    }

    public void removeContribution(Contribution contribution) {
        int indexOfContribution = contributions.indexOf(contribution);
        if (indexOfContribution >= 0) {
            contributions.remove(indexOfContribution);
            notifyDataSetChanged();
        }
    }

    public void setOnContributionRemoveListener(OnContributionRemoveListener onContributionRemoveListener) {
        this.onContributionRemoveListener = onContributionRemoveListener;
    }

    public void setOnContributionDenounceListener(OnContributionDenounceListener onContributionDenounceListener) {
        this.onContributionDenounceListener = onContributionDenounceListener;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView contribution;
        private final TextView author;
        private final ImageView picture;
        private final TextView date;
        private final ImageView options;

        private final DateFormatter dateFormatter;

        public ItemViewHolder(View itemView) {
            super(itemView);

            dateFormatter = new DateFormatter();

            picture = (ImageView) itemView.findViewById(R.id.picture);
            contribution = (TextView) itemView.findViewById(R.id.contribution);
            contribution.setMovementMethod(LinkMovementMethod.getInstance());
            author = (TextView) itemView.findViewById(R.id.tags);
            date = (TextView) itemView.findViewById(R.id.date);

            options = itemView.findViewById(R.id.moderationOptions);
            options.setVisibility(View.VISIBLE);
        }

        private void bindView(Contribution contribution) {
            final PopupMenu popupMenu = new PopupMenu(itemView.getContext(), options, Gravity.CENTER);
            popupMenu.inflate(UserManager.canModerate() || contribution.getAuthor().getKey().equals(UserManager.getUserId())
                    ? R.menu.menu_remove_contribution : R.menu.menu_denounce_contribution);
            options.setOnClickListener(view -> popupMenu.show());
            popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);

            if (contribution.getAuthor() != null) {
                ImageLoader.loadPersonPictureToImageView(picture, contribution.getAuthor().getPicture());
                this.author.setText(contribution.getAuthor().getNickname());
            }
            this.contribution.setText(contribution.getContent());

            String timeElapsed = dateFormatter.getTimeElapsed(contribution.getCreatedDate()
                    , itemView.getContext().getString(R.string.date_now));
            this.date.setText(timeElapsed.toLowerCase());
        }

        private PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.removeContribution:
                        if (onContributionRemoveListener != null)
                            onContributionRemoveListener.onContributionRemove(contributions.get(getLayoutPosition()));
                        break;
                    case R.id.denounceContribution:
                        if (onContributionDenounceListener != null)
                            onContributionDenounceListener.onContributionDenounce(contributions.get(getLayoutPosition()));
                        break;
                }
                return true;
            }
        };
    }

    public interface OnContributionRemoveListener {
        void onContributionRemove(Contribution contribution);
    }

    public interface OnContributionDenounceListener {
        void onContributionDenounce(Contribution contribution);
    }
}
