package in.ureport.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.ureport.R;
import in.ureport.UreportApplication;
import in.ureport.activities.AnswerPollActivity;
import in.ureport.activities.PollResultsActivity;
import in.ureport.loader.PollsLoader;
import in.ureport.models.Poll;
import in.ureport.models.User;
import in.ureport.views.adapters.PollAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class PollsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Poll>>, PollAdapter.PollParticipationListener {

    private static final String EXTRA_USER = "user";

    private static final int LOADER_ID_POLLS = 20;

    private RecyclerView pollsList;

    private User user;
    private boolean publicType = true;

    public static PollsFragment newInstance(User user) {
        PollsFragment pollsFragment = new PollsFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);
        pollsFragment.setArguments(args);

        return pollsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if(extras != null && extras.containsKey(EXTRA_USER)) {
            user = extras.getParcelable(EXTRA_USER);
            publicType = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polls, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_ID_POLLS, null, this).forceLoad();
    }

    private void setupView(View view) {
        pollsList = (RecyclerView) view.findViewById(R.id.pollsList);
        pollsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public Loader<List<Poll>> onCreateLoader(int id, Bundle args) {
        if(publicType) {
            return new PollsLoader(getActivity());
        } else {
            return new PollsLoader(getActivity(), user);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Poll>> loader, List<Poll> data) {
        PollAdapter adapter = new PollAdapter(data);
        adapter.setPollParticipationListener(this);
        pollsList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Poll>> loader) {}

    @Override
    public void onParticipate(Poll poll) {
        if(UreportApplication.validateUserLogin(getActivity())) {
            Intent answerPollIntent = new Intent(getActivity(), AnswerPollActivity.class);
            answerPollIntent.putExtra(AnswerPollActivity.EXTRA_POLL, poll);
            startActivity(answerPollIntent);
        }
    }

    @Override
    public void onSeeResults(Poll poll) {
        Intent pollResultsIntent = new Intent(getActivity(), PollResultsActivity.class);
        pollResultsIntent.putExtra(AnswerPollActivity.EXTRA_POLL, poll);
        startActivity(pollResultsIntent);
    }
}
