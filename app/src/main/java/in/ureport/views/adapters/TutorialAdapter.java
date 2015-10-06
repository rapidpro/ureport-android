package in.ureport.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import in.ureport.fragments.TutorialItemFragment;
import in.ureport.models.holders.Tutorial;

/**
 * Created by johncordeiro on 05/10/15.
 */
public class TutorialAdapter extends FragmentStatePagerAdapter {

    private final List<Tutorial> tutorialList;

    public TutorialAdapter(FragmentManager fm, List<Tutorial> tutorialList) {
        super(fm);
        this.tutorialList = tutorialList;
    }

    @Override
    public Fragment getItem(int position) {
        return TutorialItemFragment.newInstance(tutorialList.get(position));
    }

    @Override
    public int getCount() {
        return tutorialList.size();
    }
}
