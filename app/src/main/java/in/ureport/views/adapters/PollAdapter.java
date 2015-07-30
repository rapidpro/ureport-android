package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.ureport.R;
import in.ureport.models.Poll;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CURRENT_POLL = 0;
    private static final int TYPE_PAST_POLL = 1;

    private List<Poll> polls;
    private boolean publicType = true;

    private DateFormat dateFormatter;

    private PollParticipationListener pollParticipationListener;

    public PollAdapter(List<Poll> polls) {
        this.polls = polls;
        dateFormatter = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
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
            case TYPE_CURRENT_POLL:
                ((CurrentPollViewHolder)holder).bindView(polls.get(position));
                break;
            default:
            case TYPE_PAST_POLL:
                ((PastPollViewHolder)holder).bindView(polls.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return polls.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(isCurrentPoll(polls.get(position)))
            return TYPE_CURRENT_POLL;
        return TYPE_PAST_POLL;
    }

    private boolean isCurrentPoll(Poll poll) {
        return poll.getExpirationDate().after(new Date());
    }

    public void setPublicType(boolean publicType) {
        this.publicType = publicType;
    }

    private class PastPollViewHolder extends RecyclerView.ViewHolder {

        private final TextView info;
        private final TextView description;
        private final View infoBackground;
        private final TextView category;

        public PastPollViewHolder(View itemView) {
            super(itemView);


            info = (TextView) itemView.findViewById(R.id.info);
            category = (TextView) itemView.findViewById(R.id.category);
            description = (TextView) itemView.findViewById(R.id.description);
            infoBackground = itemView.findViewById(R.id.infoBackground);

            View previousPollsTitle = itemView.findViewById(R.id.previousPollsTitle);
            previousPollsTitle.setVisibility(publicType ? View.VISIBLE : View.GONE);

            Button results = (Button) itemView.findViewById(R.id.results);
            results.setOnClickListener(onSeeResultsClickListener);
        }

        private void bindView(Poll poll) {
            category.setText(poll.getCategory().getName());
            infoBackground.setBackgroundResource(poll.getCategory().getColor());
            description.setText(poll.getDescription());
            info.setText(dateFormatter.format(poll.getExpirationDate()));
        }

        private View.OnClickListener onSeeResultsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pollParticipationListener != null)
                    pollParticipationListener.onSeeResults(polls.get(getLayoutPosition()));
            }
        };
    }

    private class CurrentPollViewHolder extends RecyclerView.ViewHolder {

        private final View pollCover;
        private final TextView category;
        private final ImageView icon;

        private final TextView description;

        private final RadioButton checkYes;
        private final RadioButton checkNo;

        public CurrentPollViewHolder(View itemView) {
            super(itemView);

            description = (TextView) itemView.findViewById(R.id.description);

            Button participate = (Button) itemView.findViewById(R.id.participate);
            participate.setOnClickListener(onParticipateClickListener);

            pollCover = itemView.findViewById(R.id.pollCover);
            category = (TextView) itemView.findViewById(R.id.category);
            icon = (ImageView) itemView.findViewById(R.id.icon);

            checkYes = (RadioButton) itemView.findViewById(R.id.checkYes);
            checkYes.setOnClickListener(onCheckClickListener);
            checkNo = (RadioButton) itemView.findViewById(R.id.checkNo);
            checkNo.setOnClickListener(onCheckClickListener);
        }

        private void bindView(Poll poll) {
            pollCover.setBackgroundColor(itemView.getContext().getResources().getColor(poll.getCategory().getColor()));
            category.setText(poll.getCategory().getName());
            icon.setImageResource(poll.getCategory().getIcon());

            description.setText(poll.getDescription());
        }

        private View.OnClickListener onCheckClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == checkYes)
                    checkNo.setChecked(false);
                else
                    checkYes.setChecked(false);
            }
        };

        private View.OnClickListener onParticipateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkYes.isChecked() && !checkNo.isChecked()) {
                    Toast.makeText(itemView.getContext(), R.string.answer_poll_choose_error, Toast.LENGTH_LONG).show();
                } else if (pollParticipationListener != null)
                    pollParticipationListener.onParticipate(polls.get(getLayoutPosition()));
            }
        };
    }

    public void setPollParticipationListener(PollParticipationListener pollParticipationListener) {
        this.pollParticipationListener = pollParticipationListener;
    }

    public interface PollParticipationListener {
        void onParticipate(Poll poll);
        void onSeeResults(Poll poll);
    }
}
