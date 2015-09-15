package in.ureport.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.activities.AnswerPollActivity;
import in.ureport.activities.PollResultsActivity;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.models.Poll;
import in.ureport.models.User;
import in.ureport.network.PollServices;
import in.ureport.views.adapters.PollAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class PollsFragment extends Fragment implements PollAdapter.PollParticipationListener {

    private static final String TAG = "PollsFragment";
    private static final String EXTRA_USER = "user";

    private RecyclerView pollsList;
    private ProgressBar progressBar;

    private PollServices pollServices;

    public static PollsFragment newInstance(User user) {
        PollsFragment pollsFragment = new PollsFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);
        pollsFragment.setArguments(args);

        return pollsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polls, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
        loadData();
    }

    private void loadData() {
        pollServices.getPolls(onPollsLoadedListener);
    }

    private void setupObjects() {
        pollServices = new PollServices();
    }

    private void setupView(View view) {
        pollsList = (RecyclerView) view.findViewById(R.id.pollsList);
        pollsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }

    private void setupPolls(List<Poll> polls) {
        String [] pollColors = getResources().getStringArray(R.array.poll_colors);

        PollAdapter pollsAdapter = new PollAdapter(polls, pollColors);
        pollsAdapter.setPollParticipationListener(PollsFragment.this);
        pollsList.setAdapter(pollsAdapter);
    }

    private void addPollIfPossible(List<Poll> polls, DataSnapshot snapshot) {
        try {
            Poll poll = snapshot.getValue(Poll.class);
            poll.setKey(snapshot.getKey());
            polls.add(poll);
        } catch(Exception exception) {
            Log.e(TAG, "onDataChange ", exception);
        }
    }

    @Override
    public void onPollRespond(String message) {}

    @Override
    public void onSeeResults(Poll poll) {
        Intent pollResultsIntent = new Intent(getActivity(), PollResultsActivity.class);
        pollResultsIntent.putExtra(AnswerPollActivity.EXTRA_POLL, poll);
        startActivity(pollResultsIntent);
    }

    private ValueEventListenerAdapter onPollsLoadedListener = new ValueEventListenerAdapter() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            super.onDataChange(dataSnapshot);
            updateViewForData();

            List<Poll> polls = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                addPollIfPossible(polls, snapshot);
            }
            setupPolls(polls);
        }
    };

    private void updateViewForData() {
        progressBar.setVisibility(View.GONE);
    }

}
