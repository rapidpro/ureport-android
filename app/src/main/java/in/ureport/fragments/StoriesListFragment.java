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
import in.ureport.activities.StoryViewActivity;
import in.ureport.listener.FloatingActionButtonListener;
import in.ureport.loader.StoriesLoader;
import in.ureport.managers.RecyclerScrollListener;
import in.ureport.managers.UserManager;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class StoriesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Story>>
        , StoriesAdapter.OnStoryViewListener {

    private static final int LOADER_ID_STORIES_LIST = 10;
    private static final String EXTRA_USER = "user";

    private RecyclerView storiesList;
    private View info;

    private User user;
    private boolean publicType = true;

    private StoriesAdapter.OnPublishStoryListener onPublishStoryListener;

    private FloatingActionButtonListener floatingActionButtonListener;
    private StoriesAdapter adapter;

    public static StoriesListFragment newInstance(User user) {
        StoriesListFragment storiesListFragment = new StoriesListFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);
        storiesListFragment.setArguments(args);

        return storiesListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if(extras != null && extras.containsKey(EXTRA_USER)) {
            user = extras.getParcelable(EXTRA_USER);
            publicType = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_stories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);
        loadStories();
    }

    public void loadStories() {
        getLoaderManager().initLoader(LOADER_ID_STORIES_LIST, null, this).forceLoad();
    }

    private void setupView(View view) {
        storiesList = (RecyclerView) view.findViewById(R.id.storiesList);
        storiesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        storiesList.addOnScrollListener(new RecyclerScrollListener(floatingActionButtonListener));

        info = view.findViewById(R.id.info);
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        StoriesLoader loader;
        if(publicType)
            loader = new StoriesLoader(getActivity());
        else
            loader = new StoriesLoader(getActivity(), user);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> data) {
        if(data != null && !data.isEmpty()) {
            adapter = new StoriesAdapter(data);
            if(needsUserPublish()) adapter.setUser(user);
            adapter.setOnStoryViewListener(this);
            adapter.setOnPublishStoryListener(onPublishStoryListener);
            storiesList.setAdapter(adapter);
            info.setVisibility(View.GONE);
        } else {
            info.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {}

    @Override
    public void onStoryViewClick(Story story) {
        Intent storyViewIntent = new Intent(getActivity(), StoryViewActivity.class);
        storyViewIntent.putExtra(StoryViewActivity.EXTRA_STORY, story);
        storyViewIntent.putExtra(StoryViewActivity.EXTRA_USER, user);
        startActivity(storyViewIntent);
    }

    public void setOnPublishStoryListener(StoriesAdapter.OnPublishStoryListener onPublishStoryListener) {
        this.onPublishStoryListener = onPublishStoryListener;
    }

    public void setFloatingActionButtonListener(FloatingActionButtonListener floatingActionButtonListener) {
        this.floatingActionButtonListener = floatingActionButtonListener;
    }

    public void updateUser(User user) {
        this.user = user;
        if(adapter != null && needsUserPublish()) adapter.setUser(user);
    }

    private boolean needsUserPublish() {
        return publicType && UserManager.userLoggedIn && user != null;
    }
}
