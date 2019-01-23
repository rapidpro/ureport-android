package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.FlowManager;
import in.ureport.models.Poll;
import in.ureport.network.PollServices;
import in.ureport.tasks.CleanMessageNotificationTask;
import in.ureport.views.adapters.PollAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class PollsResultsFragment extends Fragment {

    private static final String TAG = "PollsResultsFragment";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pollsList;
    private ProgressBar progressBar;

    private PollServices pollServices;

    private PollAdapter.PollParticipationListener pollParticipationListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polls_results, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        setupObjects();
    }

    @Override
    public void onResume() {
        super.onResume();
        cleanMessageNotifications();
    }

    private void cleanMessageNotifications() {
        CleanMessageNotificationTask cleanMessageNotificationTask = new CleanMessageNotificationTask(getActivity());
        cleanMessageNotificationTask.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FlowManager.enableFlowNotificiation();
        if (onPollsLoadedListener != null) pollServices.removePollsListener(onPollsLoadedListener);
    }

    private void loadData() {
        pollServices.getPolls(onPollsLoadedListener);
    }

    private void setupObjects() {
        pollServices = new PollServices();
        loadData();
    }

    private void setupView(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        pollsList = (RecyclerView) view.findViewById(R.id.pollsList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        pollsList.setLayoutManager(linearLayoutManager);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }

    private void setupPolls(List<Poll> polls) {
        Collections.reverse(polls);
        String[] pollColors = getResources().getStringArray(R.array.poll_colors);

        PollAdapter pollsAdapter = new PollAdapter(polls, pollColors);
        pollsAdapter.setPollParticipationListener(pollParticipationListener);
        pollsList.setAdapter(pollsAdapter);
    }

    private void addPollIfPossible(List<Poll> polls, DataSnapshot snapshot) {
        try {
            Poll poll = snapshot.getValue(Poll.class);
            poll.setKey(snapshot.getKey());
            polls.add(poll);
        } catch (Exception exception) {
            Log.e(TAG, "onDataChange ", exception);
        }
    }

    public void setPollParticipationListener(PollAdapter.PollParticipationListener pollParticipationListener) {
        this.pollParticipationListener = pollParticipationListener;
    }

    private void updateViewForData() {
        progressBar.setVisibility(View.GONE);
    }

    private ValueEventListenerAdapter onPollsLoadedListener = new ValueEventListenerAdapter() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            super.onDataChange(dataSnapshot);
            if (isAdded()) {
                updateViewForData();

                List<Poll> polls = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    addPollIfPossible(polls, snapshot);
                }
                setupPolls(polls);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = () -> {
        loadData();
        swipeRefreshLayout.setRefreshing(false);
    };

}