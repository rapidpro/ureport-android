package in.ureport.views.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.moxun.tagcloudlib.view.TagCloudView;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.models.MultipleResult;
import in.ureport.models.PollResult;
import in.ureport.models.KeywordsResult;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class PollResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MULTIPLE_RESULT = 0;
    private static final int TYPE_WORDS_RESULT = 1;

    private List<PollResult> results;
    private String [] colors;

    private PollResultsListener pollResultsListener;

    private boolean showResultsByRegion = false;

    private PollResultsAdapter(String [] colors) {
        this.colors = colors;
    }

    public PollResultsAdapter(List<PollResult> results, String [] colors) {
        this(colors);
        this.results = results;
    }

    public PollResultsAdapter(PollResult result, String [] colors) {
        this(colors);
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
        if(result instanceof KeywordsResult) {
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
            resultsByRegion.setVisibility(showResultsByRegion ? View.VISIBLE : View.GONE);
        }

        protected void bindView(PollResult pollResult) {
            question.setText(pollResult.getTitle());
            date.setText(pollResult.getDate());

            String infoText = itemView.getContext().getString(R.string.polls_responded_info
                    , pollResult.getResponded(), pollResult.getPolled());
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

        private final RecyclerView choicesList;

        public MultipleResultViewHolder(View itemView) {
            super(itemView);
            choicesList = (RecyclerView) itemView.findViewById(R.id.choicesList);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setAutoMeasureEnabled(true);
            choicesList.setLayoutManager(linearLayoutManager);
        }

        @Override
        protected void bindView(PollResult pollResult) {
            super.bindView(pollResult);

            MultipleResult multipleResult = (MultipleResult) pollResult;
            MultipleResultsAdapter adapter = new MultipleResultsAdapter(multipleResult.getResults(), colors);
            choicesList.setAdapter(adapter);
        }
    }

    private class WordsResultViewHolder extends ResultViewHolder {

        private final TagCloudView tagCloudView;

        public WordsResultViewHolder(View itemView) {
            super(itemView);
            tagCloudView = (TagCloudView) itemView.findViewById(R.id.tag_cloud);
        }

        @Override
        protected void bindView(PollResult pollResult) {
            super.bindView(pollResult);

            KeywordsResult keywordsResult = (KeywordsResult) pollResult;
            tagCloudView.setAdapter(new PollWordsAdapter(keywordsResult.getResults()));
        }
    }

    public void setShowResultsByRegion(boolean showResultsByRegion) {
        this.showResultsByRegion = showResultsByRegion;
    }

    public void setPollResultsListener(PollResultsListener pollResultsListener) {
        this.pollResultsListener = pollResultsListener;
    }

    public interface PollResultsListener {
        void onViewResultByRegion(PollResult pollResult);
    }
}
