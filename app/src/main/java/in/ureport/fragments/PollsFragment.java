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
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

import java.util.List;

import in.ureport.R;
import in.ureport.activities.AnswerPollActivity;
import in.ureport.activities.PollResultsActivity;
import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.loader.PollsLoader;
import in.ureport.models.Poll;
import in.ureport.models.User;
import in.ureport.models.rapidpro.Message;
import in.ureport.network.RapidProServices;
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

    private PollAdapter pollsAdapter;

    private RapidProServices rapidProServices;

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

        setupObjects();
        setupView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rapidProServices.removeLastMessageChildEventListener(onLastMessageChildEventListener);
    }

    private void setupObjects() {
        rapidProServices = new RapidProServices();
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
        pollsAdapter = new PollAdapter(data);
        pollsAdapter.setPollParticipationListener(this);
        pollsList.setAdapter(pollsAdapter);
        rapidProServices.addLastMessageChildEventListener(onLastMessageChildEventListener);
    }

    @Override
    public void onLoaderReset(Loader<List<Poll>> loader) {}

    @Override
    public void onPollRespond(String message) {
        rapidProServices.sendMessage(getActivity(), message);
        Toast.makeText(getActivity(), R.string.response_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSeeResults(Poll poll) {
        Intent pollResultsIntent = new Intent(getActivity(), PollResultsActivity.class);
        pollResultsIntent.putExtra(AnswerPollActivity.EXTRA_POLL, poll);
        startActivity(pollResultsIntent);
    }

    private ChildEventListenerAdapter onLastMessageChildEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
            super.onChildAdded(dataSnapshot, previousChildKey);
            updateLastMessageBySnapshot(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
            super.onChildChanged(dataSnapshot, previousChildKey);
            updateLastMessageBySnapshot(dataSnapshot);
        }
    };

    private void updateLastMessageBySnapshot(DataSnapshot dataSnapshot) {
        Message lastMessage = dataSnapshot.getValue(Message.class);
        lastMessage.setKey(dataSnapshot.getKey());
        pollsAdapter.setLastMessage(lastMessage);
        pollsList.scrollToPosition(0);
    }
}
