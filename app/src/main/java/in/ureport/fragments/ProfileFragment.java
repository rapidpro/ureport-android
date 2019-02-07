package in.ureport.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.ilhasoft.support.tool.ResourceUtil;
import in.ureport.R;
import in.ureport.activities.ProfileActivity;
import in.ureport.helpers.ImageLoader;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.models.holders.NavigationItem;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.NavigationAdapter;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class ProfileFragment extends ProgressFragment {

    public static final String TAG = "ProfileFragment";

    private static final String EXTRA_USER = "user";
    private static final int RANKING_POSITION = 1;

    private TextView name;
    private TextView location;
    private ViewPager pager;
    private TextView points;
    private TextView ranking;
    private TextView stories;
    private ImageView picture;
    private TabLayout tabs;

    private User user;

    private static ValueEventListenerAdapter firebaseValueEventListenerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle extras = getArguments();
        if (extras != null && extras.containsKey(EXTRA_USER)) {
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
        setupContextDependencies();
        loadUser();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_my_profile, menu);
        final int color = new ResourceUtil(requireContext()).getColorByAttr(R.attr.colorPrimary);
        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        menu.findItem(R.id.edit).getIcon().setColorFilter(colorFilter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            displayUpdateOptions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupContextDependencies() {
        firebaseValueEventListenerAdapter = new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                user = dataSnapshot.getValue(User.class);
                if (isAdded()) updateUser(user);
            }
        };
    }

    public void loadUser() {
        if (firebaseValueEventListenerAdapter == null)
            return;

        UserServices userServices = new UserServices();
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseValueEventListenerAdapter.onDataChange(dataSnapshot);
            }
        });
        userServices.getRankingQuery().addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                final List<User> users = new ArrayList<>(1000);
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    users.add(data.getValue(User.class));
                }
                Collections.reverse(users);
                if (!isAdded()) {
                    return;
                }
                int userPosition = users.indexOf(user) + 1;
                ranking.setText(makeUserMetricTextTemplate(
                        String.valueOf(userPosition).concat("º"),
                        getString(R.string.profile_ranking).toLowerCase()
                ));
            }
        });
    }

    private void setupView(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        picture = view.findViewById(R.id.picture);
        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);

        points = view.findViewById(R.id.points);
        ranking = view.findViewById(R.id.ranking);
        stories = view.findViewById(R.id.storiesCount);

        pager = view.findViewById(R.id.pager);
        tabs = view.findViewById(R.id.tabs);

        final int primaryColorRes = new ResourceUtil(requireContext()).getColorByAttr(R.attr.colorPrimary);
        tabs.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.gray), primaryColorRes);
        tabs.setSelectedTabIndicatorColor(primaryColorRes);
    }

    private void updateUser(User user) {
        setupPagerWithUser(user);

        name.setText(user.getNickname());
        ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());

        final String country = CountryProgramManager.getCurrentCountryProgram().getName();
        location.setText(country.concat(", ").concat(user.getState()));

        final String pointsCount = String.valueOf(getIntegerValue(user.getPoints()));
        final String storiesCount = String.valueOf(getIntegerValue(user.getStories()));

        points.setText(makeUserMetricTextTemplate(pointsCount, getString(R.string.label_view_points).toLowerCase()));
        ranking.setText(makeUserMetricTextTemplate("0º", getString(R.string.profile_ranking).toLowerCase()));
        stories.setText(makeUserMetricTextTemplate(storiesCount, getString(R.string.label_view_stories).toLowerCase()));
    }

    private CharSequence makeUserMetricTextTemplate(final String count, final String label) {
        final SpannableString spannableString = new SpannableString(count.concat("\n").concat(label));
        spannableString.setSpan(new RelativeSizeSpan(2.1f), 0, count.length(), 0);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, count.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, count.length(), 0);
        return spannableString;
    }

    private int getIntegerValue(Integer value) {
        return value != null ? value : 0;
    }

    private void setupPagerWithUser(User user) {
        NavigationItem storiesItem = new NavigationItem(StoriesListFragment.newInstance(user), getString(R.string.profile_my_stories));
        NavigationItem rankingItem = new NavigationItem(RankingFragment.newInstance(user), getString(R.string.profile_ranking));

        NavigationAdapter navigationAdapter = new NavigationAdapter(getChildFragmentManager(), storiesItem, rankingItem);
        pager.setAdapter(navigationAdapter);
        pager.setOffscreenPageLimit(2);
        tabs.setupWithViewPager(pager);

        checkRankingAction();
    }

    private void checkRankingAction() {
        String action = getActivity().getIntent().getAction();
        if (action != null && action.equals(ProfileActivity.ACTION_DISPLAY_RANKING)) {
            pager.setCurrentItem(RANKING_POSITION);
        }
    }

    public void displayUpdateOptions() {
        final String[] items = new String[]{
                getString(R.string.title_pref_edit_profile),
                getString(R.string.title_pref_change_password)
        };
        new AlertDialog.Builder(requireContext())
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        addEditProfileFragment(user);
                    } else {
                        addChangePasswordFragment();
                    }
                })
                .show();
    }

    private void addEditProfileFragment(final User user) {
        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) return;

        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, EditUserFragment.newInstance(user))
                .commit();
    }

    private void addChangePasswordFragment() {
        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) return;

        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, new ChangePasswordFragment())
                .commit();
    }

}
