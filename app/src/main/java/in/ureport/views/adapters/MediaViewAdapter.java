package in.ureport.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import in.ureport.fragments.MediaFragment;
import in.ureport.models.Media;

/**
 * Created by johncordeiro on 04/09/15.
 */
public class MediaViewAdapter extends FragmentStatePagerAdapter {

    private List<Media> medias;

    public MediaViewAdapter(FragmentManager fm, List<Media> medias) {
        super(fm);
        this.medias = medias;
    }

    @Override
    public Fragment getItem(int position) {
        if(position < getCount()-1) {
            Media media = medias.get(position);
            return MediaFragment.newInstance(media);
        }
        return new MediaFragment();
    }

    @Override
    public int getCount() {
        return medias.size() + 1;
    }
}
