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
        return new ItemViewHolder(inflater.inflate(R.layout.item_contribution, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder)holder).bindView(contributions.get(position));
    }

    @Override
    public long getItemId(int position) {
        return contributions.get(position).getKey().hashCode();
    }

    @Override
    public int getItemCount() {
        if(contributions == null) return 0;
        return contributions.size();
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
}
