package in.ureport.listener;

import in.ureport.models.Story;

/**
 * Created by john-mac on 8/23/16.
 */
public interface OnNeedUpdateStoryListener {

    void loadStoryData(final Story story, OnStoryUpdatedListener listener);

}
