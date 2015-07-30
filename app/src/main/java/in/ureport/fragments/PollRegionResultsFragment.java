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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import in.ureport.R;
import in.ureport.models.PollResult;
import in.ureport.views.adapters.PollResultsAdapter;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class PollRegionResultsFragment extends Fragment {

    private static final String EXTRA_POLL_RESULT = "pollResult";
    private PollResult pollResult;

    public static PollRegionResultsFragment newInstance(PollResult pollResult) {
        PollRegionResultsFragment pollRegionResultsFragment = new PollRegionResultsFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_POLL_RESULT, pollResult);
        pollRegionResultsFragment.setArguments(args);

        return pollRegionResultsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_POLL_RESULT)) {
            pollResult = getArguments().getParcelable(EXTRA_POLL_RESULT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poll_region_results, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.label_poll_region_results);
    }

    private void setupView(View view) {
        Spinner location = (Spinner) view.findViewById(R.id.location);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.poll_results_regions, R.layout.view_simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.view_simple_spinner_dropdown_item);
        location.setAdapter(adapter);

        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.resultsList);
        resultsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        PollResultsAdapter pollResultsAdapter = new PollResultsAdapter(pollResult);
        pollResultsAdapter.setShowResultsByRegion(false);
        resultsList.setAdapter(pollResultsAdapter);
    }
}
