package in.ureport.views.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private static final int TYPE_CURRENT_POLL = 0;
    private static final int TYPE_PAST_POLL = 1;

    private List<Poll> polls;
    private String[] pollColors;

    private Map<PollCategory, Integer> colorMap = new HashMap<>();
    private PollParticipationListener pollParticipationListener;

    private boolean currentPollEnabled = false;

    public PollAdapter(List<Poll> polls, String [] pollColors) {
        this.polls = polls;
        this.pollColors = pollColors;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_CURRENT_POLL) {
            return new CurrentPollViewHolder(inflater.inflate(R.layout.item_current_poll, parent, false));
        } else {
            return new PastPollViewHolder(inflater.inflate(R.layout.item_past_poll, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case TYPE_PAST_POLL:
                ((PastPollViewHolder)holder).bindView(polls.get(getPollPosition(position)));
        }
    }

    @Override
    public long getItemId(int position) {
        if(getItemViewType(position) == TYPE_CURRENT_POLL) {
            return R.layout.item_current_poll;
        }
        return polls.get(getPollPosition(position)).hashCode();
    }

    private int getPollPosition(int position) {
        return isCurrentPollEnabled() ? position-1 : position;
    }

    private boolean isCurrentPollEnabled() {
        return currentPollEnabled;
    }

    public void setCurrentPollEnabled(boolean currentPollEnabled) {
        this.currentPollEnabled = currentPollEnabled;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return isCurrentPollEnabled() ? polls.size() + 1 : polls.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(isCurrentPollEnabled() && position == 0) return TYPE_CURRENT_POLL;
        return TYPE_PAST_POLL;
    }

    private class PastPollViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView category;
        private final TextView info;
        private final View infoBackground;

        public PastPollViewHolder(View itemView) {
            super(itemView);

            info = (TextView) itemView.findViewById(R.id.info);
            category = (TextView) itemView.findViewById(R.id.category);
            title = (TextView) itemView.findViewById(R.id.description);
            infoBackground = itemView.findViewById(R.id.infoBackground);

            Button results = (Button) itemView.findViewById(R.id.results);
            results.setOnClickListener(onSeeResultsClickListener);
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
            if(categoryColor != null) {
                return categoryColor;
            } else {
                int layoutPosition = getLayoutPosition();
                layoutPosition = currentPollEnabled ? layoutPosition - 1 : layoutPosition;

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
                    Poll poll = polls.get(isCurrentPollEnabled() ? getLayoutPosition() - 1 : getLayoutPosition());
                    pollParticipationListener.onSeeResults(poll);
                }
            }
        };
    }

    private class CurrentPollViewHolder extends RecyclerView.ViewHolder {
        public CurrentPollViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setPollParticipationListener(PollParticipationListener pollParticipationListener) {
        this.pollParticipationListener = pollParticipationListener;
    }

    public interface PollParticipationListener {
        void onSeeResults(Poll poll);
    }
}
