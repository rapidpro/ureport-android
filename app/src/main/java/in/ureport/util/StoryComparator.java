package in.ureport.util;

import java.util.Comparator;

import in.ureport.models.Story;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class StoryComparator implements Comparator<Story> {

    @Override
    public int compare(Story story1, Story story2) {
        return story2.getCreatedDate().compareTo(story1.getCreatedDate());
    }

}
