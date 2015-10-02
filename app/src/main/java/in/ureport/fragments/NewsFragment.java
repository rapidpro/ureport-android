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

import java.util.List;

import in.ureport.R;
import in.ureport.activities.StatisticsActivity;
import in.ureport.activities.StoryViewActivity;
import in.ureport.loader.NewsLoader;
import in.ureport.models.News;
import in.ureport.models.User;
import in.ureport.views.adapters.NewsAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>>, NewsAdapter.NewsListener {

    private static final String TAG = "NewsFragment";

    private static final String EXTRA_USER = "user";
    private static final int LOADER_ID_NEWS = 30;

    private User user;
    private RecyclerView newsList;

    private NewsAdapter adapter;

    public static NewsFragment newInstance(User user) {
        NewsFragment newsFragment = new NewsFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);
        newsFragment.setArguments(args);

        return newsFragment;
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
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_ID_NEWS, null, this).forceLoad();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        newsList = (RecyclerView) view.findViewById(R.id.newsList);
        newsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        adapter = new NewsAdapter(data, user);
        adapter.setNewsListener(this);
        newsList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {}

    public void setUser(User user) {
        this.user = user;

        if(adapter != null)
            adapter.setUser(user);
    }

    @Override
    public void onAddStatistics() {
        Intent statisticsIntent = new Intent(getActivity(), StatisticsActivity.class);
        statisticsIntent.putExtra(StatisticsActivity.EXTRA_USER, user);
        startActivity(statisticsIntent);
    }

    @Override
    public void onReadNews(News news) {
        Intent newsViewIntent = new Intent(getActivity(), StoryViewActivity.class);
        newsViewIntent.putExtra(StoryViewActivity.EXTRA_NEWS, news);
        startActivity(newsViewIntent);
    }
}
