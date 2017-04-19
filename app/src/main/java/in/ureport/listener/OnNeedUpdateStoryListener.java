package in.ureport.listener;

import in.ureport.models.Story;
import in.ureport.models.holders.StoryHolder;

/**
 * Created by john-mac on 8/23/16.
 */
public interface OnNeedUpdateStoryListener {

    StoryHolder loadStoryData(final Story story);

}
