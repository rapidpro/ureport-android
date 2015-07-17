package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class StatisticsFragment extends Fragment {

    private static final String EXTRA_USER = "user";

    private User user;

    public static StatisticsFragment newInstance(User user) {
        StatisticsFragment statisticsFragment = new StatisticsFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);
        statisticsFragment.setArguments(args);

        return statisticsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null && args.containsKey(EXTRA_USER) && user == null) {
            user = args.getParcelable(EXTRA_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView countryProgram = (TextView) view.findViewById(R.id.countryProgram);
        countryProgram.setText(getString(R.string.news_ureporters, user.getCountry()));
    }
}
