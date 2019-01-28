package in.ureport.tasks;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import in.ureport.R;
import in.ureport.models.Story;
import in.ureport.views.holders.StoryItemViewHolder;

/**
 * Created by johncordeiro on 8/3/15.
 */
public class ShareStoryTask extends ShareViewTask<Story> {

    public ShareStoryTask(Fragment fragment, Story object) {
        super(fragment, object, R.string.title_share_story);
    }

    @Override
    protected View createViewForObject(Story object) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        StoryItemViewHolder itemViewHolder = new StoryItemViewHolder(inflater.inflate(R.layout.item_story_, null), null, null, null);
        itemViewHolder.bind(object);
        itemViewHolder.bindInfo(R.string.story_share_info);
        return itemViewHolder.itemView;
    }
}
