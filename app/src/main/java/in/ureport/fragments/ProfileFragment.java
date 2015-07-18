package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.models.User;
import in.ureport.models.holders.NavigationItem;
import in.ureport.views.adapters.NavigationAdapter;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class ProfileFragment extends Fragment {

    private static final String EXTRA_USER = "user";

    private TextView name;
    private TextView firstLetter;
    private ViewPager pager;
    private TextView points;
    private TextView polls;
    private TextView stories;
    private TabLayout tabs;

    private User user;

    public static ProfileFragment newInstance(User user) {
        ProfileFragment profileFragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_USER, user);
        profileFragment.setArguments(args);

        return profileFragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        updateUser(user);
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");

        name = (TextView)view.findViewById(R.id.name);
        firstLetter = (TextView)view.findViewById(R.id.firstLetter);

        points = (TextView) view.findViewById(R.id.points);
        polls = (TextView) view.findViewById(R.id.polls);
        stories = (TextView) view.findViewById(R.id.stories);

        pager = (ViewPager)view.findViewById(R.id.pager);
        tabs = (TabLayout)view.findViewById(R.id.tabs);
    }

    public void updateUser(User user) {
        if(user != null) {
            setupPagerWithUser(user);

            name.setText("@"+user.getUsername());
            firstLetter.setText(user.getUsername().toUpperCase());

            points.setText(getString(R.string.menu_points, user.getPoints()));
            polls.setText(getString(R.string.profile_polls, user.getPolls()));
            stories.setText(getString(R.string.profile_stories, user.getStories()));
        }
    }

    private void setupPagerWithUser(User user) {
        NavigationItem storiesItem = new NavigationItem(StoriesListFragment.newInstance(user), getString(R.string.profile_my_stories));
        NavigationItem pollsItem = new NavigationItem(PollsFragment.newInstance(user), getString(R.string.profile_answered_polls));
        NavigationItem rankingItem = new NavigationItem(RankingFragment.newInstance(user), getString(R.string.profile_ranking));

        NavigationAdapter navigationAdapter = new NavigationAdapter(getFragmentManager(), storiesItem, pollsItem, rankingItem);
        pager.setAdapter(navigationAdapter);
        tabs.setupWithViewPager(pager);
    }
}
