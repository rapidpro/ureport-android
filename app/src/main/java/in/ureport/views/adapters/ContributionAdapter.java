package in.ureport.views.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ItemViewHolder(inflater.inflate(R.layout.item_contribution, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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
        private final ImageView action;

        private final DateFormatter dateFormatter;

        public ItemViewHolder(View itemView) {
            super(itemView);

            dateFormatter = new DateFormatter();

            picture = itemView.findViewById(R.id.authorPicture);
            contribution = itemView.findViewById(R.id.contribution);
            contribution.setMovementMethod(LinkMovementMethod.getInstance());
            author = itemView.findViewById(R.id.authorName);
            date = itemView.findViewById(R.id.date);
            action = itemView.findViewById(R.id.action);
        }

        private void bindView(Contribution contribution) {
            final Context context = itemView.getContext();
            if (UserManager.canModerate() || checkCurrentUserContribution(contribution)) {
                action.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_remove));
                action.setOnClickListener(onRemoveContributionClickListener);
            } else {
                action.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_report));
                action.setOnClickListener(onDenounceContributionClickListener);
            }

            if (contribution.getAuthor() != null) {
                ImageLoader.loadPersonPictureToImageView(picture, contribution.getAuthor().getPicture());
                this.author.setText(contribution.getAuthor().getNickname());
            }
            this.contribution.setText(contribution.getContent());

            String timeElapsed = dateFormatter.getTimeElapsed(contribution.getCreatedDate()
                    , itemView.getContext().getString(R.string.date_now));
            this.date.setText(timeElapsed.toLowerCase());
        }

        private boolean checkCurrentUserContribution(final Contribution contribution) {
            return contribution.getAuthor().getKey().equals(UserManager.getUserId());
        }

        private View.OnClickListener onRemoveContributionClickListener = view -> {
            if (onContributionRemoveListener == null) {
                return;
            }
            new AlertDialog.Builder(itemView.getContext())
                    .setMessage(R.string.question_delete_contribution)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        onContributionRemoveListener
                                .onContributionRemove(contributions.get(getLayoutPosition()));
                    })
                    .setNegativeButton(R.string.no, ((dialog, which) -> dialog.dismiss()))
                    .show();
        };

        private View.OnClickListener onDenounceContributionClickListener = view -> {
            if (onContributionDenounceListener == null) {
                return;
            }
            new AlertDialog.Builder(itemView.getContext())
                    .setMessage(R.string.question_denounce_contribution)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        onContributionDenounceListener
                                .onContributionDenounce(contributions.get(getLayoutPosition()));
                    })
                    .setNegativeButton(R.string.no, ((dialog, which) -> dialog.dismiss()))
                    .show();
        };
    }

    public interface OnContributionRemoveListener {
        void onContributionRemove(Contribution contribution);
    }

    public interface OnContributionDenounceListener {
        void onContributionDenounce(Contribution contribution);
    }
}
