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

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.activities.BaseActivity;
import in.ureport.loader.StoriesLoader;
import in.ureport.models.Story;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class StoriesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Story>> {

    private RecyclerView storiesList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_stories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private void setupView(View view) {
        storiesList = (RecyclerView) view.findViewById(R.id.storiesList);
        storiesList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        return new StoriesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> data) {
        StoriesAdapter adapter = new StoriesAdapter(data);
        storiesList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {}
}
