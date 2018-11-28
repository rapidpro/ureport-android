package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcorei.infinitefire.InfiniteFireArray;

import in.ureport.R;
import in.ureport.helpers.DividerItemDecoration;
import in.ureport.models.User;
import in.ureport.network.UserServices;
import in.ureport.views.widgets.InfiniteFireLinearRecyclerView;
import in.ureport.views.adapters.RankingAdapter;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class RankingFragment extends Fragment {

    private static final String EXTRA_USER = "user";

    private InfiniteFireLinearRecyclerView rankingList;

    private UserServices userServices;

    public static RankingFragment newInstance(User user) {
        RankingFragment rankingFragment = new RankingFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);
        rankingFragment.setArguments(args);

        return rankingFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObjects();
        setupView(view);
        loadData();
    }

    private void setupObjects() {
        userServices = new UserServices();
    }

    private void setupView(View view) {
        rankingList = (InfiniteFireLinearRecyclerView) view.findViewById(R.id.rankingList);
        rankingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rankingList.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    private void loadData() {
        InfiniteFireArray<User> rankingArray = new InfiniteFireArray<>(User.class
                , userServices.getRankingQuery(), 50, 50, false, false);

        RankingAdapter rankingAdapter = new RankingAdapter(rankingArray);
        rankingAdapter.setHasStableIds(true);

        rankingList.setAdapter(rankingAdapter);
        rankingList.setInfiniteFireArray(rankingArray);
    }
}
