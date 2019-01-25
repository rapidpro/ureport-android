package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import br.com.ilhasoft.support.tool.ResourceUtil;
import in.ureport.R;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.models.holders.NavigationItem;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.NavigationAdapter;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    private StoriesListFragment storiesListFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        loadData();
    }

    private void setupView(final View view) {
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(CountryProgramManager.getCurrentCountryProgram().getName());

        final ViewPager pager = view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);

        NavigationAdapter adapter = new NavigationAdapter(getChildFragmentManager(), getNavigationItems());
        pager.setAdapter(adapter);

        final TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager);

        final int primaryColorRes = new ResourceUtil(requireContext()).getColorByAttr(R.attr.colorPrimary);
        tabLayout.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.gray), primaryColorRes);
        tabLayout.setSelectedTabIndicatorColor(primaryColorRes);
    }

    @NonNull
    private NavigationItem[] getNavigationItems() {
        storiesListFragment = new StoriesListFragment();
        final NavigationItem storiesItem = new NavigationItem(
                storiesListFragment, getString(R.string.main_stories));
        final NavigationItem pollsItem = new NavigationItem(
                new PollsFragment(), getString(R.string.main_polls));

        return new NavigationItem[]{storiesItem, pollsItem};
    }

    private void loadData() {
        if (!UserManager.isUserLoggedIn()) {
            return;
        }
        UserServices userServices = new UserServices();
        userServices.getUser(UserManager.getUserId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = refreshUserCountry(dataSnapshot.getValue(User.class));
                storiesListFragment.updateUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @NonNull
    private User refreshUserCountry(User user) {
        if (user == null) {
            user = new User();
            user.setCountry(Locale.getDefault().getDisplayCountry());
        }
        return user;
    }

}
