package in.ureport.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.activities.PollResultsActivity;
import in.ureport.models.Poll;
import in.ureport.views.adapters.PollAdapter;

/**
 * Created by john-mac on 4/25/16.
 */
public class PollsFragment extends Fragment implements PollAdapter.PollParticipationListener {

    private TextView infoChoosePoll;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_polls, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        PollsResultsFragment resultsFragment = (PollsResultsFragment) getChildFragmentManager()
                .findFragmentById(R.id.pollsResultsFragment);
        resultsFragment.setPollParticipationListener(this);

        infoChoosePoll = (TextView) view.findViewById(R.id.infoChoosePoll);
        setupInfoVisibility();
    }

    private void setupInfoVisibility() {
        if(infoChoosePoll != null) {
            Fragment fragment = getChildFragmentManager().findFragmentById(R.id.pollResultsDetailsContainer);
            infoChoosePoll.setVisibility(fragment != null ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onSeeResults(Poll poll) {
        if(infoChoosePoll != null) {
            infoChoosePoll.setVisibility(View.GONE);

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
