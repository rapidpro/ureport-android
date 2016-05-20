package in.ureport.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.ureport.R;
import in.ureport.activities.BaseActivity;
import in.ureport.flowrunner.fragments.FlowFragment;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRuleset;
import in.ureport.flowrunner.models.FlowStepSet;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.loader.LastFlowLoader;
import in.ureport.managers.FlowManager;
import in.ureport.managers.UserManager;
import in.ureport.models.Poll;
import in.ureport.network.PollServices;
import in.ureport.tasks.CleanMessageNotificationTask;
import in.ureport.tasks.MessageNotificationTask;
import in.ureport.tasks.NotificationTask;
import in.ureport.tasks.SendFlowReponsesTask;
import in.ureport.views.adapters.PollAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class PollsResultsFragment extends Fragment implements LoaderManager.LoaderCallbacks<FlowDefinition>
        , FlowFragment.FlowListener {

    private static final String TAG = "PollsResultsFragment";

    private static final int LAST_POLL_LOADER = 5048;

    private RecyclerView pollsList;
    private ProgressBar progressBar;
    private TextView title;
    private TextView subtitle;
    private NestedScrollView scrollView;

    private PollServices pollServices;
    private PollAdapter pollsAdapter;

    private PollAdapter.PollParticipationListener pollParticipationListener;

    private boolean hasCurrentPoll = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polls_results, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
        registerNotificationReceiver();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && scrollView != null)
            scrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        cleanMessageNotifications();
        loadData();
    }

    private void cleanMessageNotifications() {
        CleanMessageNotificationTask cleanMessageNotificationTask = new CleanMessageNotificationTask(getActivity());
        cleanMessageNotificationTask.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FlowManager.enableFlowNotificiation();
        getActivity().unregisterReceiver(onReloadNotifications);
        if(onPollsLoadedListener != null) pollServices.removePollsListener(onPollsLoadedListener);
    }

    private void loadData() {
        pollServices.getPolls(onPollsLoadedListener);
        loadLastFlow(false);
    }

    private void loadLastFlow(boolean forceLoad) {
        if(UserManager.isUserLoggedIn() && UserManager.isUserCountryProgramEnabled()) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.topBar);
            if(!forceLoad && fragment != null && fragment instanceof FlowFragment) {
                FlowFragment flowFragment = (FlowFragment)fragment;
                flowFragment.setFlowListener(this);
                setCurrentPollExistance(true);
                showTitle(flowFragment.getFlowDefinition());
            } else {
                getLoaderManager().initLoader(LAST_POLL_LOADER, null, this).forceLoad();
            }
        }
    }

    private void setCurrentPollExistance(boolean hasCurrentPoll) {
        this.hasCurrentPoll = hasCurrentPoll;
        if(pollsAdapter != null) {
            pollsAdapter.setCurrentPollEnabled(hasCurrentPoll);
        }
        scrollView.smoothScrollTo(0, 0);
    }

    private void setupObjects() {
        pollServices = new PollServices();
    }

    private void setupView(View view) {
        scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);

        pollsList = (RecyclerView) view.findViewById(R.id.pollsList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        pollsList.setLayoutManager(linearLayoutManager);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        title = (TextView) view.findViewById(R.id.title);
        subtitle = (TextView) view.findViewById(R.id.subtitle);
    }

    private void setupPolls(List<Poll> polls) {
        Collections.reverse(polls);
        String [] pollColors = getResources().getStringArray(R.array.poll_colors);

        pollsAdapter = new PollAdapter(polls, pollColors);
        pollsAdapter.setPollParticipationListener(pollParticipationListener);
        pollsAdapter.setCurrentPollEnabled(hasCurrentPoll);
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

    @Override
    public Loader<FlowDefinition> onCreateLoader(int id, Bundle args) {
        return new LastFlowLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<FlowDefinition> loader, FlowDefinition flowDefinition) {
        if(flowDefinition != null) {
            showTitle(flowDefinition);
            addFlowDefinition(flowDefinition);
        } else {
            hideTitle();
        }
    }

    private void hideTitle() {
        title.setVisibility(View.GONE);
        subtitle.setVisibility(View.GONE);
    }

    private void showTitle(FlowDefinition flowDefinition) {
        title.setVisibility(View.VISIBLE);
        subtitle.setVisibility(View.VISIBLE);
        subtitle.setText(flowDefinition.getMetadata().getName());
    }

    private void addFlowDefinition(final FlowDefinition flowDefinition) {
        setCurrentPollExistance(true);
        progressBar.post(() -> {
            FlowFragment flowFragment = FlowFragment.newInstance(flowDefinition, UserManager.getUserLanguage());
            flowFragment.setFlowListener(PollsResultsFragment.this);
            getFragmentManager().beginTransaction()
                    .replace(R.id.topBar, flowFragment)
                    .commit();
        });
    }

    private void registerNotificationReceiver() {
        IntentFilter intentFilter = new IntentFilter(BaseActivity.ACTION_RELOAD_NOTIFICATIONS);
        getActivity().registerReceiver(onReloadNotifications, intentFilter);
    }

    @Override
    public void onLoaderReset(Loader<FlowDefinition> loader) {}

    @Override
    public void onFlowLanguageChanged(String iso3Language) {
        UserManager.updateUserLanguage(iso3Language);
    }

    @Override
    public void onFlowResponse(FlowRuleset ruleset) {
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onFlowFinished(FlowStepSet stepSet) {
        stepSet.setContact(UserManager.getUserRapidUuid());
        SendFlowReponsesTask sendFlowReponsesTask = new SendFlowReponsesTask(getActivity()) {
            @Override
            protected void onPostExecute(Boolean successResult) {
                super.onPostExecute(successResult);
                if(successResult) {
                    displayMessage(R.string.message_response_sent);
                } else {
                    displayMessage(R.string.error_no_internet);
                }
            }
        };
        sendFlowReponsesTask.execute(stepSet);
    }

    @Override
    public void onFinishedClick() {}

    private BroadcastReceiver onReloadNotifications = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(NotificationTask.EXTRA_TYPE);
            if(type != null && type.equals(MessageNotificationTask.NEW_MESSAGE_TYPE)) {
                loadLastFlow(true);
            }
        }
    };

    public void setPollParticipationListener(PollAdapter.PollParticipationListener pollParticipationListener) {
        this.pollParticipationListener = pollParticipationListener;
    }

    private void displayMessage(@StringRes int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}