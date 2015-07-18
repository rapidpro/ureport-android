package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.ureport.R;
import in.ureport.models.Poll;
import in.ureport.models.PollResult;
import in.ureport.views.adapters.PollResultsAdapter;
import in.ureport.views.widgets.ContentPager;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class PollAllResultsFragment extends Fragment {

    private static final String EXTRA_POLL = "poll";

    private Poll poll;

    private PollResultsAdapter.PollResultsListener pollResultsListener;

    public static PollAllResultsFragment newInstance(Poll poll) {
        PollAllResultsFragment pollAllResultsFragment = new PollAllResultsFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_POLL, poll);
        pollAllResultsFragment.setArguments(args);

        return pollAllResultsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_POLL)) {
            poll = getArguments().getParcelable(EXTRA_POLL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poll_all_results, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.label_poll_results);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.resultsList);
        resultsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        PollResultsAdapter pollResultsAdapter = new PollResultsAdapter(poll.getResults());
        pollResultsAdapter.setPollResultsListener(pollResultsListener);
        resultsList.setAdapter(pollResultsAdapter);
    }

    public void setPollResultsListener(PollResultsAdapter.PollResultsListener pollResultsListener) {
        this.pollResultsListener = pollResultsListener;
    }
}
