package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.ureport.R;
import in.ureport.models.Poll;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Poll> polls;

    private DateFormat dateFormatter;
    private NumberFormat numberFormatter;

    private PollParticipationListener pollParticipationListener;

    public PollAdapter(List<Poll> polls) {
        this.polls = polls;
        dateFormatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
        numberFormatter = NumberFormat.getIntegerInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_poll, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(polls.get(position));
    }

    @Override
    public int getItemCount() {
        return polls.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView description;
        private TextView info;
        private TextView responseRate;
        private TextView responded;
        private Button participate;

        public ViewHolder(View itemView) {
            super(itemView);

            info = (TextView) itemView.findViewById(R.id.info);
            description = (TextView) itemView.findViewById(R.id.description);
            responseRate = (TextView) itemView.findViewById(R.id.responseRate);
            responded = (TextView) itemView.findViewById(R.id.responded);
            participate = (Button) itemView.findViewById(R.id.participate);
        }

        private void bindView(Poll poll) {
            description.setText(poll.getDescription());

            setInfo(poll);
            setParticipate(poll);

            responseRate.setText(itemView.getContext().getString(R.string.polls_response_rate, poll.getResponseRate()));

            String respondedText = numberFormatter.format(poll.getResponded());
            String polledText = numberFormatter.format(poll.getPolled());
            responded.setText(itemView.getContext().getString(R.string.polls_responded_info, respondedText, polledText));
        }

        private void setParticipate(Poll poll) {
            boolean current = poll.getExpirationDate().after(new Date());
            if(current) {
                participate.setText(R.string.polls_participate);
                participate.setOnClickListener(onParticipateClickListener);
            } else {
                participate.setText(R.string.polls_see_results);
            }
        }

        private void setInfo(Poll poll) {
            boolean current = poll.getExpirationDate().after(new Date());
            if(current) {
                info.setText(itemView.getContext().getString(R.string.polls_current));
            } else {
                info.setText(dateFormatter.format(poll.getExpirationDate()));
            }
        }

        private View.OnClickListener onParticipateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pollParticipationListener != null)
                    pollParticipationListener.onParticipate(polls.get(getLayoutPosition()));
            }
        };
    }

    public void setPollParticipationListener(PollParticipationListener pollParticipationListener) {
        this.pollParticipationListener = pollParticipationListener;
    }

    public interface PollParticipationListener {
        void onParticipate(Poll poll);
    }
}
