package in.ureport.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ilhasoft.support.tool.EditTextValidator;
import in.ureport.R;
import in.ureport.models.Poll;
import in.ureport.models.PollCategory;
import in.ureport.models.rapidpro.Message;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CURRENT_POLL = 0;
    private static final int TYPE_PAST_POLL = 1;

    private List<Poll> polls;
    private String[] categoryColors;

    private Map<PollCategory, Integer> colorMap = new HashMap<>();

    private Message lastMessage;

    private PollParticipationListener pollParticipationListener;

    private boolean changed = false;

    public PollAdapter(List<Poll> polls, String [] categoryColors) {
        this.polls = polls;
        this.categoryColors = categoryColors;
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
            case TYPE_CURRENT_POLL:
                ((CurrentPollViewHolder)holder).bindView(lastMessage);
                break;
            default:
            case TYPE_PAST_POLL:
                ((PastPollViewHolder)holder).bindView(polls.get(getPollPosition(position)));
        }
    }

    @Override
    public long getItemId(int position) {
        if(getItemViewType(position) == TYPE_CURRENT_POLL) {
            return lastMessage.getKey().hashCode();
        }
        return polls.get(getPollPosition(position)).hashCode();
    }

    private int getPollPosition(int position) {
        return hasCurrentPoll() ? position-1 : position;
    }

    private boolean hasCurrentPoll() {
        return false;
    }

    @Override
    public int getItemCount() {
        return hasCurrentPoll() ? polls.size() + 1 : polls.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(hasCurrentPoll() && position == 0) return TYPE_CURRENT_POLL;
        return TYPE_PAST_POLL;
    }

    public void setLastMessage(Message lastMessage) {
        if(!hasCurrentPoll()) {
            this.lastMessage = lastMessage;
            notifyDataSetChanged();
        } else {
            changed = true;
            this.lastMessage = lastMessage;
            notifyItemChanged(0, lastMessage);
        }
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
                int colorIndex = (getLayoutPosition() % categoryColors.length);
                int color = Color.parseColor(categoryColors[colorIndex]);
                colorMap.put(pollCategory, color);

                return color;
            }
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

        private final TextView description;
        private final EditText message;

        public CurrentPollViewHolder(View itemView) {
            super(itemView);

            description = (TextView) itemView.findViewById(R.id.description);

            Button send = (Button) itemView.findViewById(R.id.send);
            send.setOnClickListener(onSendClickListener);

            message = (EditText) itemView.findViewById(R.id.message);
            message.setOnEditorActionListener(onMessageEditorActionListener);
        }

        private void bindView(Message lastMessage) {
            description.setText(lastMessage.getText());

            if(changed) {
                showKeyboard();
            }
        }

        private void showKeyboard() {
            message.requestFocus();
            message.setError(null);

            InputMethodManager inputMethodManager = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(message, InputMethodManager.SHOW_IMPLICIT);
        }

        private View.OnClickListener onSendClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResponse();
            }
        };

        private void sendResponse() {
            if (isResponseValid() && pollParticipationListener != null) {
                pollParticipationListener.onPollRespond(message.getText().toString());

                message.setText(null);
                clearError();
            }
        }

        private void clearError() {
            message.postDelayed(new Runnable() {
                @Override
                public void run() {
                    message.setError(null);
                }
            }, 200);
        }

        private boolean isResponseValid() {
            EditTextValidator validator = new EditTextValidator();
            return validator.validateEmpty(message, itemView.getContext().getString(R.string.error_empty_message));
        }

        private TextView.OnEditorActionListener onMessageEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                sendResponse();
                return true;
            }
        };
    }

    public void setPollParticipationListener(PollParticipationListener pollParticipationListener) {
        this.pollParticipationListener = pollParticipationListener;
    }

    public interface PollParticipationListener {
        void onPollRespond(String message);
        void onSeeResults(Poll poll);
    }
}
