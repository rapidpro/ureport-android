package in.ureport.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.ureport.R;
import in.ureport.activities.PollResultsActivity;
import in.ureport.models.Poll;
import in.ureport.views.adapters.PollAdapter;

/**
 * Created by john-mac on 4/25/16.
 */
public class PollsFragment extends Fragment implements PollAdapter.PollParticipationListener {

    private View pollsResultsDetailsContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polls, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PollsResultsFragment resultsFragment = (PollsResultsFragment) getChildFragmentManager()
                .findFragmentById(R.id.pollsResultsFragment);
        resultsFragment.setPollParticipationListener(this);

        pollsResultsDetailsContainer = view.findViewById(R.id.pollResultsDetailsContainer);
    }

    @Override
    public void onSeeResults(Poll poll) {
        if(pollsResultsDetailsContainer != null) {
            PollAllResultsFragment pollsResultsFragment = PollAllResultsFragment.newInstance(poll);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.pollResultsDetailsContainer, pollsResultsFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else {
            Intent pollResultsIntent = new Intent(getActivity(), PollResultsActivity.class);
            pollResultsIntent.putExtra(PollResultsActivity.EXTRA_POLL, poll);
            startActivity(pollResultsIntent);
        }
    }
}
