package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.views.holders.StoryItemViewHolder;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private List<Story> stories;

    private boolean publicType = false;
    private boolean moderationType = false;

    private User user;

    private OnStoryViewListener onStoryViewListener;
    private OnPublishStoryListener onPublishStoryListener;
    private StoryModerationListener storyModerationListener;

    public StoriesAdapter() {
        setHasStableIds(true);
        this.stories = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch(type) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_story_header, viewGroup, false));
            default:
            case TYPE_ITEM:
                View view = inflater.inflate(R.layout.item_story, viewGroup, false);
                if(moderationType)
                    return new ModeratedItemViewHolder(view);
                else
                    return new StoryItemViewHolder(view, onStoryViewListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch(getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder)viewHolder).bind(user);
                break;
            default:
            case TYPE_ITEM:
                ((StoryItemViewHolder)viewHolder).bind(stories.get(getListPosition(position)));
        }
    }

    private int getListPosition(int position) {
        if(publicType) return position-1;
        return position;
    }

    @Override
    public long getItemId(int position) {
        return getItemViewType(position) == TYPE_HEADER
                ? 0 : stories.get(getListPosition(position)).getKey().hashCode();
    }

    @Override
    public int getItemCount() {
        if(publicType) return stories.size() + 1;
        return stories.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(publicType && position == 0) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public void setOnStoryViewListener(OnStoryViewListener onStoryViewListener) {
        this.onStoryViewListener = onStoryViewListener;
    }

    public void setOnPublishStoryListener(OnPublishStoryListener onPublishStoryListener) {
        this.onPublishStoryListener = onPublishStoryListener;
    }

    public void updateStory(Story story) {
        int indexOfStory = stories.indexOf(story);
        if(indexOfStory >= 0) {
            stories.set(indexOfStory, story);
            notifyItemChanged(publicType ? indexOfStory + 1 : indexOfStory);
        }
    }

    public void removeStory(Story story) {
        int indexOfStory = stories.indexOf(story);
        if(indexOfStory >= 0) {
            stories.remove(indexOfStory);
            notifyItemRemoved(indexOfStory);
        }
    }

    public void addStory(Story story) {
        this.stories.add(0, story);

        int firstStoryIndex = publicType ? 1 : 0;
        notifyItemInserted(firstStoryIndex);
    }

    public void enableModerationMode(StoryModerationListener storyModerationListener) {
        this.storyModerationListener = storyModerationListener;
        moderationType = true;
    }

    public void setUser(User user) {
        this.user = user;
        this.publicType = true;
        notifyItemInserted(0);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;
        private final TextView name;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            picture = (ImageView) itemView.findViewById(R.id.picture);
            name = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(onPublishStoryClickListener);
        }

        private void bind(User user) {
            name.setHint(itemView.getContext().getString(R.string.list_stories_header_title, user.getNickname()));
            ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());
        }

        private View.OnClickListener onPublishStoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPublishStoryListener != null)
                    onPublishStoryListener.onPublishStory();
            }
        };
    }

    private class ModeratedItemViewHolder extends StoryItemViewHolder {

        public ModeratedItemViewHolder(View itemView) {
            super(itemView, onStoryViewListener);

            Button readFullStory = (Button) itemView.findViewById(R.id.readFullStory);
            readFullStory.setVisibility(View.GONE);

            View storyModeration = itemView.findViewById(R.id.storyModeration);
            storyModeration.setVisibility(View.VISIBLE);

            Button publish = (Button) itemView.findViewById(R.id.publish);
            publish.setOnClickListener(onApproveClickListener);

            Button disapprove = (Button) itemView.findViewById(R.id.disapprove);
            disapprove.setOnClickListener(onDisapproveClickListener);
        }

        private View.OnClickListener onApproveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyModerationListener != null) {
                    storyModerationListener.onApprove(stories.get(getLayoutPosition()));
                }
            }
        };

        private View.OnClickListener onDisapproveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyModerationListener != null) {
                    storyModerationListener.onDisapprove(stories.get(getLayoutPosition()));
                }
            }
        };
    }

    public interface StoryModerationListener {
        void onApprove(Story story);
        void onDisapprove(Story story);
    }

    public interface OnStoryViewListener {
        void onStoryViewClick(Story story);
    }

    public interface OnPublishStoryListener {
        void onPublishStory();
    }
}
