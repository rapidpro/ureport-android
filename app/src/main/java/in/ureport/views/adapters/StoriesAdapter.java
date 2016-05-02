package in.ureport.views.adapters;

import android.support.v4.util.Pair;
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
import in.ureport.activities.MainActivity;
import in.ureport.fragments.StoriesListFragment;
import in.ureport.helpers.ImageLoader;
import in.ureport.listener.OnUserStartChattingListener;
import in.ureport.models.News;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.views.holders.NewsItemViewHolder;
import in.ureport.views.holders.StoryItemViewHolder;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_STORY = 1;
    private static final int TYPE_NEWS = 2;

    private List<Story> stories;
    private List<News> news;

    private boolean publicType = false;
    private boolean moderationType = false;

    private User user;

    private OnStoryViewListener onStoryViewListener;
    private OnNewsViewListener onNewsViewListener;
    private StoriesListFragment.OnPublishStoryListener onPublishStoryListener;
    private StoryModerationListener storyModerationListener;
    private OnUserStartChattingListener onUserStartChattingListener;
    private OnShareNewsListener onShareNewsListener;

    public StoriesAdapter() {
        setHasStableIds(true);
        this.stories = new ArrayList<>();
        this.news = new ArrayList<>();
    }

    public StoriesAdapter(User user, List<Story> stories, List<News> news) {
        setHasStableIds(true);
        this.stories = stories;
        this.news = news;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch(type) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_story_header, viewGroup, false));
            case TYPE_NEWS:
                View newsView = inflater.inflate(R.layout.item_news, viewGroup, false);
                return new NewsItemViewHolder(newsView, onNewsViewListener, onShareNewsListener);
            default:
            case TYPE_STORY:
                View view = inflater.inflate(R.layout.item_story, viewGroup, false);
                if(moderationType)
                    return new ModeratedItemViewHolder(view);
                else
                    return new StoryItemViewHolder(view, onStoryViewListener, onUserStartChattingListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch(getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder)viewHolder).bind(user);
                break;
            case TYPE_STORY:
                ((StoryItemViewHolder)viewHolder).bind(stories.get(getStoryPosition(position)));
                break;
            case TYPE_NEWS:
                ((NewsItemViewHolder)viewHolder).bind(news.get(getNewsPosition(position)));
        }
    }

    private int getNewsPosition(int position) {
        return position - getLastStoryPosition();
    }

    private int getStoryPosition(int position) {
        if(publicType) return position-1;
        return position;
    }

    @Override
    public long getItemId(int position) {
        switch(getItemViewType(position)) {
            case TYPE_HEADER:
                return 0;
            case TYPE_NEWS:
                return news.get(getNewsPosition(position)).getId();
            default:
            case TYPE_STORY:
                return stories.get(getStoryPosition(position)).getKey().hashCode();
        }
    }

    @Override
    public int getItemCount() {
        if(publicType) return getDataCount() + 1;
        return getDataCount();
    }

    private int getDataCount() {
        return stories.size() + news.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(publicType && position == 0) {
            return TYPE_HEADER;
        } else if(isNewsPosition(position)) {
            return TYPE_NEWS;
        } else {
            return TYPE_STORY;
        }
    }

    private boolean isNewsPosition(int position) {
        return position >= getLastStoryPosition();
    }

    private int getLastStoryPosition() {
        return publicType ? stories.size()+1 : stories.size();
    }

    public void setOnNewsViewListener(OnNewsViewListener onNewsViewListener) {
        this.onNewsViewListener = onNewsViewListener;
    }

    public void setOnStoryViewListener(OnStoryViewListener onStoryViewListener) {
        this.onStoryViewListener = onStoryViewListener;
    }

    public void setOnPublishStoryListener(StoriesListFragment.OnPublishStoryListener onPublishStoryListener) {
        this.onPublishStoryListener = onPublishStoryListener;
    }

    public void setOnUserStartChattingListener(OnUserStartChattingListener onUserStartChattingListener) {
        this.onUserStartChattingListener = onUserStartChattingListener;
    }

    public void setOnShareNewsListener(OnShareNewsListener onShareNewsListener) {
        this.onShareNewsListener = onShareNewsListener;
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

    public void addNews(List<News> news) {
        this.news.addAll(news);
        notifyDataSetChanged();
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
            super(itemView, onStoryViewListener, onUserStartChattingListener);

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

    public List<News> getNews() {
        return news;
    }

    public List<Story> getStories() {
        return stories;
    }

    public interface StoryModerationListener {
        void onApprove(Story story);
        void onDisapprove(Story story);
    }

    public interface OnStoryViewListener {
        void onStoryViewClick(Story story, Pair<View, String>... views);
    }

    public interface OnNewsViewListener {
        void onNewsViewClick(News news, Pair<View, String>... views);
    }

    public interface OnShareNewsListener {
        void onShareNews(News news);
    }

}
