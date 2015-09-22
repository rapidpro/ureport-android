package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import in.ureport.R;
import in.ureport.models.User;
import in.ureport.helpers.DividerItemDecoration;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.RankingAdapter;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class RankingFragment extends Fragment {

    private static final String EXTRA_USER = "user";

    private User user;

    private RecyclerView rankingList;

    private UserServices userServices;

    public static RankingFragment newInstance(User user) {
        RankingFragment rankingFragment = new RankingFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);
        rankingFragment.setArguments(args);

        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if(extras != null && extras.containsKey(EXTRA_USER)) {
            user = extras.getParcelable(EXTRA_USER);
        }
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

    private void loadData() {
        userServices.loadRanking(new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(List<User> users) {
                Collections.reverse(users);
                RankingAdapter rankingAdapter = new RankingAdapter(users);
                rankingList.setAdapter(rankingAdapter);
            }
        });
    }

    private void setupObjects() {
        userServices = new UserServices();
    }

    private void setupView(View view) {
        rankingList = (RecyclerView) view.findViewById(R.id.rankingList);
        rankingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rankingList.addItemDecoration(new DividerItemDecoration(getActivity()));
    }
}
