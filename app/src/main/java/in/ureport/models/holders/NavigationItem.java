package in.ureport.models.holders;

import android.support.v4.app.Fragment;

/**
 * Created by johncordeiro on 7/13/15.
 */
public class NavigationItem {

    private Fragment fragment;

    private String title;

    public NavigationItem(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getTitle() {
        return title;
    }
}
