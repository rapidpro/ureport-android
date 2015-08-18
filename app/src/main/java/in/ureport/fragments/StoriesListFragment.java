package in.ureport.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.activities.StoryViewActivity;
import in.ureport.listener.FloatingActionButtonListener;
import in.ureport.managers.RecyclerScrollListener;
import in.ureport.managers.UserManager;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.network.StoryServices;
import in.ureport.network.UserServices;
import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class StoriesListFragment extends Fragment implements StoriesAdapter.OnStoryViewListener {

    private static final String EXTRA_USER = "user";

    private RecyclerView storiesList;
    private View info;

    private User user;
    private boolean publicType = true;

    private StoriesAdapter.OnPublishStoryListener onPublishStoryListener;

    private FloatingActionButtonListener floatingActionButtonListener;
    private StoriesAdapter adapter;

    private StoryServices storyServices;
    private UserServices userServices;

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

        setupObjects();
        setupView(view);
        loadStories();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        storyServices.removeChildEventListener(childEventListener);
    }

    private void setupObjects() {
        storyServices = new StoryServices();
        userServices = new UserServices();
    }

    public void loadStories() {
        storyServices.addChildEventListener(childEventListener);
    }

    private void setupView(View view) {
        storiesList = (RecyclerView) view.findViewById(R.id.storiesList);
        storiesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        storiesList.addOnScrollListener(new RecyclerScrollListener(floatingActionButtonListener));
        setupStoriesAdapter();

        info = view.findViewById(R.id.info);
    }

    private void setupStoriesAdapter() {
        adapter = new StoriesAdapter();
        adapter.setHasStableIds(true);
        if(needsUserPublish()) adapter.setUser(user);
        adapter.setOnStoryViewListener(this);
        adapter.setOnPublishStoryListener(onPublishStoryListener);
        storiesList.setAdapter(adapter);
    }

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

    private void updateStories(List<Story> stories) {
        if(!needsUserPublish())
            info.setVisibility(stories != null && !stories.isEmpty() ? View.GONE : View.VISIBLE);

        adapter.addStories(stories);
    }

    private boolean needsUserPublish() {
        return publicType && UserManager.userLoggedIn && user != null;
    }

    private ChildEventListener childEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previous) {
            super.onChildAdded(dataSnapshot, previous);
            updateStoryFromSnapshot(dataSnapshot);
        }
    };

    private void updateStoryFromSnapshot(DataSnapshot dataSnapshot) {
        Story story = dataSnapshot.getValue(Story.class);
        story.setKey(dataSnapshot.getKey());

        List<Story> stories = new ArrayList<>();
        stories.add(story);
        loadUsersFromStories(stories, onAfterUsersLoadedListener);
    }

    private OnAfterUsersLoadedListener onAfterUsersLoadedListener = new OnAfterUsersLoadedListener() {
        @Override
        public void onAfterUsersLoaded(List<Story> stories) {
            updateStories(stories);
        }
    };

    private void loadUsersFromStories(final List<Story> stories, final OnAfterUsersLoadedListener onAfterUsersLoadedListener) {
        for (int position = 0; position < stories.size(); position++) {
            final Story story = stories.get(position);
            final int index = position;

            userServices.getUser(story.getUser().getKey(), new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    story.setUser(user);

                    if(index == stories.size()-1) onAfterUsersLoadedListener.onAfterUsersLoaded(stories);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });
        }
    }

    private interface OnAfterUsersLoadedListener {
        void onAfterUsersLoaded(List<Story> stories);
    }
}
