package in.ureport.views.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ureport.R;
import in.ureport.models.Poll;
import in.ureport.models.PollCategory;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Poll> polls;
    private String[] pollColors;

    private Map<PollCategory, Integer> colorMap = new HashMap<>();
    private PollParticipationListener pollParticipationListener;

    public PollAdapter(List<Poll> polls, String[] pollColors) {
        this.polls = polls;
        this.pollColors = pollColors;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PastPollViewHolder(inflater.inflate(R.layout.item_past_poll, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PastPollViewHolder) holder).bindView(polls.get(position));
    }

    @Override
    public long getItemId(int position) {
        return polls.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return polls.size();
    }

    private class PastPollViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView category;
        private final TextView info;
        private final View infoBackground;
        private final CardView cardView;

        PastPollViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.pollResults);
            info = (TextView) itemView.findViewById(R.id.info);
            category = (TextView) itemView.findViewById(R.id.category);
            title = (TextView) itemView.findViewById(R.id.description);
            infoBackground = itemView.findViewById(R.id.infoBackground);

            cardView.setOnClickListener(onSeeResultsClickListener);
        }

        private void bindView(Poll poll) {
            title.setText(poll.getTitle());
            category.setText(poll.getCategory().getName());

            infoBackground.setBackgroundColor(getColorByCategory(poll.getCategory()));
            bindInfo(poll);
        }

        private void bindInfo(Poll poll) {
            String expirationDate = poll.getExpiration_date();
            info.setText(expirationDate != null ? expirationDate.toLowerCase() : "");
        }

        private int getColorByCategory(PollCategory pollCategory) {
            Integer categoryColor = colorMap.get(pollCategory);
            if (categoryColor != null) {
                return categoryColor;
            } else {
                int layoutPosition = getLayoutPosition();

                int colorIndex = (layoutPosition % pollColors.length);
                int color = Color.parseColor(pollColors[colorIndex]);
                colorMap.put(pollCategory, color);

                return color;
            }
        }

        private View.OnClickListener onSeeResultsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pollParticipationListener != null) {
                    Poll poll = polls.get(getLayoutPosition());
                    pollParticipationListener.onSeeResults(poll);
                }
            }
        };
    }

    public void setPollParticipationListener(PollParticipationListener pollParticipationListener) {
        this.pollParticipationListener = pollParticipationListener;
    }

    public interface PollParticipationListener {
        void onSeeResults(Poll poll);
    }
}
