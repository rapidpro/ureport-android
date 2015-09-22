package in.ureport.fragments;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;

import in.ureport.R;
import in.ureport.activities.ProfileActivity;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.helpers.ImageLoader;
import in.ureport.listener.OnEditProfileListener;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.models.holders.NavigationItem;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.NavigationAdapter;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class ProfileFragment extends Fragment {

    private static final String EXTRA_USER = "user";
    private static final int RANKING_POSITION = 1;

    private TextView name;
    private ViewPager pager;
    private TextView points;
    private TextView stories;
    private ImageView picture;
    private TabLayout tabs;

    private User user;

    private OnEditProfileListener onEditProfileListener;

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
        loadUser();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnEditProfileListener) {
            onEditProfileListener = (OnEditProfileListener) context;
        }
    }

    public void loadUser() {
        UserServices userServices = new UserServices();
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                user = dataSnapshot.getValue(User.class);
                updateUser(user);
            }
        });
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");

        name = (TextView)view.findViewById(R.id.name);
        picture = (ImageView)view.findViewById(R.id.picture);

        points = (TextView) view.findViewById(R.id.points);
        stories = (TextView) view.findViewById(R.id.stories);

        pager = (ViewPager)view.findViewById(R.id.pager);
        tabs = (TabLayout)view.findViewById(R.id.tabs);

        Button logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(onLogoutClickListener);

        Button edit = (Button) view.findViewById(R.id.edit);
        edit.setOnClickListener(onEditClickListener);
    }

    private void updateUser(User user) {
        setupPagerWithUser(user);

        name.setText(user.getNickname());
        ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());

        points.setText(getString(R.string.menu_points, getIntegerValue(user.getPoints())));
        stories.setText(getString(R.string.profile_stories, getIntegerValue(user.getStories())));
    }

    private int getIntegerValue(Integer value) {
        return value != null ? value : 0;
    }

    private void setupPagerWithUser(User user) {
        NavigationItem storiesItem = new NavigationItem(StoriesListFragment.newInstance(user), getString(R.string.profile_my_stories));
        NavigationItem rankingItem = new NavigationItem(RankingFragment.newInstance(user), getString(R.string.profile_ranking));

        NavigationAdapter navigationAdapter = new NavigationAdapter(getFragmentManager(), storiesItem, rankingItem);
        pager.setAdapter(navigationAdapter);
        pager.setOffscreenPageLimit(2);
        tabs.setupWithViewPager(pager);

        checkRankingAction();
    }

    private void checkRankingAction() {
        String action = getActivity().getIntent().getAction();
        if(action != null && action.equals(ProfileActivity.ACTION_DISPLAY_RANKING)) {
            pager.setCurrentItem(RANKING_POSITION);
        }
    }

    private View.OnClickListener onLogoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            logout();
        }
    };

    private View.OnClickListener onEditClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(onEditProfileListener != null) {
                onEditProfileListener.onEditProfile(user);
            }
        }
    };

    private void logout() {
        UserManager.logout(getActivity());
        getActivity().finish();
    }
}
