package in.ureport.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.marcorei.infinitefire.InfiniteFireArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ureport.R;
import in.ureport.activities.StoryViewActivity;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnNeedUpdateStoryListener;
import in.ureport.listener.OnStoryUpdatedListener;
import in.ureport.listener.OnUserStartChattingListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.News;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.models.holders.StoryHolder;
import in.ureport.network.ContributionServices;
import in.ureport.network.Response;
import in.ureport.network.StoryServices;
import in.ureport.network.UreportServices;
import in.ureport.network.UserServices;
import in.ureport.tasks.ShareNewsTask;
import in.ureport.views.adapters.StoriesAdapter;
import in.ureport.views.widgets.InfiniteFireLinearRecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class StoriesListFragment extends ProgressFragment implements StoriesAdapter.OnStoryViewListener
        , StoriesAdapter.OnNewsViewListener, StoriesAdapter.OnShareNewsListener, OnNeedUpdateStoryListener {

    private static final String TAG = "StoriesListFragment";

    private static final String EXTRA_USER = "user";
    private static final String EXTRA_NEWS = "news";
    private static final String EXTRA_PUBLIC_TYPE = "publicType";

    private InfiniteFireLinearRecyclerView storiesList;
    private View info;

    private LinearLayoutManager layoutManager;

    private User user;
    private ArrayList<News> newsList;
    private Map<String, StoryHolder> storiesLoaded;
    protected boolean publicType = true;

    private OnPublishStoryListener onPublishStoryListener;
    protected OnUserStartChattingListener onUserStartChattingListener;

    protected StoriesAdapter storiesAdapter;

    protected StoryServices storyServices;
    protected UserServices userServices;
    protected ContributionServices contributionServices;
    protected UreportServices ureportServices;

    private boolean loadingNews = false;
    private int previousPageLoaded = 1;
    private boolean hasNewsNextPage = false;

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
        getInstanceStateObjects(savedInstanceState);
        getDataFromArguments();
    }

    private void getInstanceStateObjects(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            user = savedInstanceState.getParcelable(EXTRA_USER);
            newsList = savedInstanceState.getParcelableArrayList(EXTRA_NEWS);
            publicType = savedInstanceState.getBoolean(EXTRA_PUBLIC_TYPE);
        }
    }

    private void getDataFromArguments() {
        Bundle extras = getArguments();
        if (extras != null && extras.containsKey(EXTRA_USER)) {
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

        setupObjects();
        setupView(view);
        final Query query = loadData();
        setupStoriesAdapter(query);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            storiesAdapter.unregisterAdapterDataObserver(storiesAdapterObserver);
        } catch (Exception exception) {
            Log.e(TAG, "onDestroyView: ", exception);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_USER, user);
        outState.putParcelableArrayList(EXTRA_NEWS, (ArrayList<News>) storiesAdapter.getNews());
        outState.putBoolean(EXTRA_PUBLIC_TYPE, publicType);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnPublishStoryListener) {
            onPublishStoryListener = (OnPublishStoryListener) context;
        }

        if (context instanceof OnUserStartChattingListener) {
            onUserStartChattingListener = (OnUserStartChattingListener) context;
        }
    }

    private void setupObjects() {
        storiesLoaded = new HashMap<>();
        storyServices = new StoryServices();
        userServices = new UserServices();
        contributionServices = new ContributionServices(ContributionServices.Type.Story);
        String ureportEndpoint = getString(CountryProgramManager.getCurrentCountryProgram().getUreportEndpoint());
        ureportServices = new UreportServices(ureportEndpoint);
    }

    public Query loadData() {
        if (publicType) {
            loadNewsForPage(previousPageLoaded);
        }
        return publicType ? storyServices.getStoryReference() : storyServices.getStoryQueryByUser(user);
    }

    private void loadNewsForPage(int page) {
        if (newsList == null) {
            loadingNews = true;
            ureportServices.listNews(CountryProgramManager.getCurrentCountryProgram().getOrganization(), page)
                    .enqueue(onNewsLoadedCallback);
        }
    }

    private void setupView(View view) {
        info = view.findViewById(R.id.info);
        info.setVisibility(publicType ? View.GONE : View.VISIBLE);

        storiesList = (InfiniteFireLinearRecyclerView) view.findViewById(R.id.storiesList);

        layoutManager = new LinearLayoutManager(getActivity());
        storiesList.setLayoutManager(layoutManager);
        storiesList.addOnScrollListener(onStoriesListScrollListener);
    }

    private void setupStoriesAdapter(Query query) {
        InfiniteFireArray<Story> storyFireArray = new InfiniteFireArray<>(Story.class
                , query, 10, 10, false, false);

        storiesAdapter = new StoriesAdapter(storyFireArray, publicType);
        storiesAdapter.setHasStableIds(true);
        storiesAdapter.registerAdapterDataObserver(storiesAdapterObserver);

        if (needsUserPublish()) storiesAdapter.setUser(user);

        storiesAdapter.setOnStoryViewListener(this);
        storiesAdapter.setOnNewsViewListener(this);
        storiesAdapter.setOnShareNewsListener(this);
        storiesAdapter.setOnPublishStoryListener(onPublishStoryListener);
        storiesAdapter.setOnUserStartChattingListener(onUserStartChattingListener);
        storiesAdapter.setOnNeedUpdateStoryListener(this);
        storiesList.setAdapter(storiesAdapter);
        storiesList.setInfiniteFireArray(storyFireArray);

        if (newsList != null) {
            storiesAdapter.addNews(newsList);
        }
    }

    @Override
    public void onStoryViewClick(Story story) {
        Intent intent = new Intent(requireContext(), StoryViewActivity.class);
        intent.putExtra(StoryViewActivity.EXTRA_STORY, story);
        intent.putExtra(StoryViewActivity.EXTRA_USER, user);
        startActivity(intent);
    }

    public void updateUser(User user) {
        this.user = user;
        if (storiesAdapter != null && needsUserPublish()) storiesAdapter.setUser(user);
    }

    private boolean needsUserPublish() {
        return publicType && UserManager.isUserLoggedIn() && user != null;
    }

    @Override
    public StoryHolder loadStoryData(final Story story) {
        if (storiesLoaded.containsKey(story.getKey())) {
            StoryHolder storyLoaded = storiesLoaded.get(story.getKey());
            copyDynamicData(storyLoaded, story);
            return storyLoaded;
        } else {
            loadUsersFromStory(story, storyWithUser ->
                    contributionServices.getContributionCount(story, storyWithContributions ->
                            loadStoryLikeCount(story, storyWithLikes -> {
                                storiesLoaded.put(story.getKey(), new StoryHolder(storyWithLikes));
                                storiesAdapter.updateStory(storyWithLikes);
                            })));
            return null;
        }
    }

    private void copyDynamicData(StoryHolder fromStory, Story toStory) {
        toStory.setUserObject(fromStory.getUserObject());
        toStory.setLikes(fromStory.getLikes());
        toStory.setContributions(fromStory.getContributions());
    }

    private void loadStoryLikeCount(Story story, final OnStoryUpdatedListener onStoryUpdatedListener) {
        storyServices.loadStoryLikeCount(story, new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                story.setLikes(dataSnapshot.exists() ? (int) dataSnapshot.getChildrenCount() : 0);
                onStoryUpdatedListener.onStoryUpdated(story);
            }
        });
    }

    private void loadUsersFromStory(final Story story, final OnStoryUpdatedListener onStoryUpdatedListener) {
        userServices.getUser(story.getUser(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    story.setUserObject(user);
                    onStoryUpdatedListener.onStoryUpdated(story);
                }
            }
        });
    }

    private Callback<Response<News>>
            onNewsLoadedCallback = new Callback<Response<News>>() {
        @Override
        public void onResponse(Call<Response<News>> call, retrofit2.Response<Response<News>> response) {
            final Response<News> body = response.body();
            if (body == null) {
                return;
            }
            loadingNews = false;
            hasNewsNextPage = body.getNext() != null;
            storiesAdapter.addNews(body.getResults());
        }

        @Override
        public void onFailure(Call<Response<News>> call, Throwable t) {
            Log.e(TAG, "failure ", t);
        }
    };

    @Override
    public void onNewsViewClick(News news, Pair<View, String>... views) {
        Intent storyViewIntent = new Intent(getActivity(), StoryViewActivity.class);
        storyViewIntent.putExtra(StoryViewActivity.EXTRA_NEWS, news);

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(getActivity(), views);
        ActivityCompat.startActivity(getActivity(), storyViewIntent, optionsCompat.toBundle());
    }

    private RecyclerView.OnScrollListener onStoriesListScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            checkNewsPageLoading();
        }
    };

    private void checkNewsPageLoading() {
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

        if (hasNewsNextPage && !loadingNews) {
            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                previousPageLoaded++;
                loadNewsForPage(previousPageLoaded);
            }
        }
    }

    @Override
    public void onShareNews(News news) {
        ShareNewsTask shareNewsTask = new ShareNewsTask(this, news);
        shareNewsTask.execute();
    }

    private RecyclerView.AdapterDataObserver storiesAdapterObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            if (info.getVisibility() == View.VISIBLE) {
                info.setVisibility(View.GONE);
            }
        }
    };

    private interface OnAfterStoryLoadedListener {
        void onAfterStoryLoaded(Story story);
    }

    public interface OnPublishStoryListener {
        void onPublishStory();
    }

}
