package in.ureport.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Arrays;
import java.util.List;

import in.ureport.models.holders.NavigationItem;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class NavigationAdapter extends FragmentPagerAdapter {

    private List<NavigationItem> navigationItems;

    public NavigationAdapter(FragmentManager fm, NavigationItem... navigationItems) {
        super(fm);
        this.navigationItems = Arrays.asList(navigationItems);
    }

    @Override
    public Fragment getItem(int position) {
        return navigationItems.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return navigationItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return navigationItems.get(position).getTitle();
    }
}
