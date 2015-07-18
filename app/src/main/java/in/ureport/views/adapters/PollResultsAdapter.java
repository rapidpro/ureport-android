package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.models.MultipleResult;
import in.ureport.models.PollResult;
import in.ureport.models.WordsResult;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class PollResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MULTIPLE_RESULT = 0;
    private static final int TYPE_WORDS_RESULT = 1;

    private List<PollResult> results;

    private DateFormat dateFormatter;
    private NumberFormat numberFormat;

    private PollResultsListener pollResultsListener;

    private PollResultsAdapter() {
        dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
        numberFormat = NumberFormat.getIntegerInstance();
    }

    public PollResultsAdapter(List<PollResult> results) {
        this();
        this.results = results;
    }

    public PollResultsAdapter(PollResult result) {
        this();
        this.results = new ArrayList<>();
        this.results.add(result);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_WORDS_RESULT: return new WordsResultViewHolder(inflater.inflate(R.layout.item_poll_words_result, parent, false));
            default:
            case TYPE_MULTIPLE_RESULT: return new MultipleResultViewHolder(inflater.inflate(R.layout.item_poll_multiple_result, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ResultViewHolder)holder).bindView(results.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        PollResult result = results.get(position);
        if(result instanceof WordsResult) {
            return TYPE_WORDS_RESULT;
        } else {
            return TYPE_MULTIPLE_RESULT;
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    private class ResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView question;
        private final TextView date;
        private final TextView info;

        public ResultViewHolder(View itemView) {
            super(itemView);

            question = (TextView) itemView.findViewById(R.id.question);
            date = (TextView) itemView.findViewById(R.id.date);
            info = (TextView) itemView.findViewById(R.id.info);

            Button resultsByRegion = (Button) itemView.findViewById(R.id.resultsByRegion);
            resultsByRegion.setOnClickListener(onViewResultsByRegionClickListener);
        }

        protected void bindView(PollResult pollResult) {
            question.setText(pollResult.getQuestion().getQuestion());
            date.setText(dateFormatter.format(pollResult.getDate()));

            String infoText = itemView.getContext().getString(R.string.polls_responded_info
                    , numberFormat.format(pollResult.getResponded()), numberFormat.format(pollResult.getPolled()));
            info.setText(infoText);
        }

        private View.OnClickListener onViewResultsByRegionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pollResultsListener != null)
                    pollResultsListener.onViewResultByRegion(results.get(getLayoutPosition()));
            }
        };
    }

    private class MultipleResultViewHolder extends ResultViewHolder {
        private final ImageView image;

        public MultipleResultViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }

        @Override
        protected void bindView(PollResult pollResult) {
            super.bindView(pollResult);
            image.setImageResource(((MultipleResult) pollResult).getImage());
        }
    }

    private class WordsResultViewHolder extends ResultViewHolder {
        private static final String WORD_TEMPLATE = "%1$s. %2$s";
        private final LinearLayout wordsList;

        public WordsResultViewHolder(View itemView) {
            super(itemView);
            wordsList = (LinearLayout) itemView.findViewById(R.id.wordsList);
        }

        @Override
        protected void bindView(PollResult pollResult) {
            super.bindView(pollResult);
            setupWordsList((WordsResult) pollResult);
        }

        private void setupWordsList(WordsResult wordsResult) {
            wordsList.removeAllViews();
            for (int position = 0; position < wordsResult.getResults().size(); position++) {
                String result = wordsResult.getResults().get(position);

                TextView textView = new TextView(itemView.getContext());
                textView.setText(String.format(WORD_TEMPLATE, position+1, result));

                wordsList.addView(textView, position);
            }
        }
    }

    public void setPollResultsListener(PollResultsListener pollResultsListener) {
        this.pollResultsListener = pollResultsListener;
    }

    public interface PollResultsListener {
        void onViewResultByRegion(PollResult pollResult);
    }
}
