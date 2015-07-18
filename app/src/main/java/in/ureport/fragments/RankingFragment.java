package in.ureport.fragments;

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
import in.ureport.loader.UsersLoader;
import in.ureport.models.User;
import in.ureport.util.DividerItemDecoration;
import in.ureport.views.adapters.RankingAdapter;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class RankingFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<User>>{

    private static final String EXTRA_USER = "user";

    private User user;

    private RecyclerView rankingList;

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

        rankingList = (RecyclerView) view.findViewById(R.id.rankingList);
        rankingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rankingList.addItemDecoration(new DividerItemDecoration(getActivity()));

        getLoaderManager().initLoader(0, null, this).forceLoad();
    }


    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new UsersLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        RankingAdapter rankingAdapter = new RankingAdapter(data);
        rankingList.setAdapter(rankingAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {}
}
