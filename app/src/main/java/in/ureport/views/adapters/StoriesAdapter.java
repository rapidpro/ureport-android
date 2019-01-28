package in.ureport.views.adapters;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.marcorei.infinitefire.InfiniteFireArray;
import com.marcorei.infinitefire.InfiniteFireRecyclerViewAdapter;
import com.marcorei.infinitefire.InfiniteFireSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ureport.R;
import in.ureport.listener.OnNeedUpdateStoryListener;
import in.ureport.listener.OnUserStartChattingListener;
import in.ureport.models.News;
import in.ureport.models.Story;
import in.ureport.views.holders.NewsItemViewHolder;
import in.ureport.views.holders.StoryItemViewHolder;
import in.ureport.views.holders.StoryItemViewHolder.OnStoryLikesBindingListener;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoriesAdapter extends InfiniteFireRecyclerViewAdapter<Story> {

    private static final int TYPE_STORY = 0;
    private static final int TYPE_NEWS = 1;

    private List<News> news;
    private boolean moderationType = false;

    private OnStoryViewListener onStoryViewListener;
    private OnNewsViewListener onNewsViewListener;
    private StoryModerationListener storyModerationListener;
    private OnUserStartChattingListener onUserStartChattingListener;
    private OnShareNewsListener onShareNewsListener;
    private OnNeedUpdateStoryListener onNeedUpdateStoryListener;

    public StoriesAdapter(InfiniteFireArray<Story> snapshots) {
        this(snapshots, new ArrayList<>());
    }

    public StoriesAdapter(InfiniteFireArray<Story> snapshots, List<News> news) {
        super(snapshots, 0, 0);
        setHasStableIds(true);
        this.news = news;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch(type) {
            case TYPE_NEWS:
                View newsView = inflater.inflate(R.layout.item_news, viewGroup, false);
                return new NewsItemViewHolder(newsView, onNewsViewListener, onShareNewsListener);
            default:
                View view = inflater.inflate(R.layout.item_story_, viewGroup, false);
                if (moderationType) {
                    return new ModeratedItemViewHolder(view);
                } else {
                    final StoryItemViewHolder viewHolder = new StoryItemViewHolder(view,
                            onStoryViewListener, onUserStartChattingListener, onNeedUpdateStoryListener);
                    viewHolder.setOnStoryLikesBindingListener(onStoryLikesBindingListener);
                    return viewHolder;
                }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_STORY:
                ((StoryItemViewHolder)viewHolder).bind(getStory(position));
                break;
            case TYPE_NEWS:
                ((NewsItemViewHolder)viewHolder).bind(news.get(getNewsPosition(position)));
        }
    }

    @NonNull
    @SuppressWarnings("ConstantConditions")
    public Story getStory(int position) {
        InfiniteFireSnapshot<Story> snapshot = getItem(position);
        Story story = snapshot.getValue();
        story.setKey(snapshot.getKey());
        return story;
    }

    private int getNewsPosition(int position) {
        return position - getLastStoryPosition();
    }

    @Override
    public long getItemId(int position) {
        switch(getItemViewType(position)) {
            case TYPE_NEWS:
                return news.get(getNewsPosition(position)).getId();
            case TYPE_STORY:
                return getItem(position).getKey().hashCode();
            default:
                return position;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + news.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isNewsPosition(position)) {
            return TYPE_NEWS;
        } else {
            return TYPE_STORY;
        }
    }

    private InfiniteFireSnapshot<Story> getItem(int position) {
        return snapshots.getItem(position - indexOffset);
    }

    public void updateStory(Story story) {
        notifyItemChanged(snapshots.getIndexForKey(story.getKey()));
    }

    private boolean isNewsPosition(int position) {
        return position >= getLastStoryPosition();
    }

    private int getLastStoryPosition() {
        return super.getItemCount();
    }

    public void setOnNewsViewListener(OnNewsViewListener onNewsViewListener) {
        this.onNewsViewListener = onNewsViewListener;
    }

    public void setOnStoryViewListener(OnStoryViewListener onStoryViewListener) {
        this.onStoryViewListener = onStoryViewListener;
    }

    public void setOnUserStartChattingListener(OnUserStartChattingListener onUserStartChattingListener) {
        this.onUserStartChattingListener = onUserStartChattingListener;
    }

    public void setOnShareNewsListener(OnShareNewsListener onShareNewsListener) {
        this.onShareNewsListener = onShareNewsListener;
    }

    public void setOnNeedUpdateStoryListener(OnNeedUpdateStoryListener onNeedUpdateStoryListener) {
        this.onNeedUpdateStoryListener = onNeedUpdateStoryListener;
    }

    public void addNews(List<News> news) {
        this.news.addAll(news);
        notifyDataSetChanged();
    }

    public void enableModerationMode(StoryModerationListener storyModerationListener) {
        this.storyModerationListener = storyModerationListener;
        moderationType = true;
    }

    private OnStoryLikesBindingListener onStoryLikesBindingListener = new OnStoryLikesBindingListener() {

        private Map<String, Boolean> map = new HashMap<>();

        @Override
        public void addLike(String storyKey, Boolean like) {
            map.put(storyKey, like);
        }

        @Override
        public boolean checkLike(String storyKey) {
            final Boolean value = map.get(storyKey);
            return value != null && value;
        }

        @Override
        public boolean checkStoryKey(String storyKey) {
            return map.containsKey(storyKey);
        }
    };

    private class ModeratedItemViewHolder extends StoryItemViewHolder {

        private Story story;

        public ModeratedItemViewHolder(View itemView) {
            super(itemView, onStoryViewListener, onUserStartChattingListener, onNeedUpdateStoryListener);

            Button readFullStory = (Button) itemView.findViewById(R.id.readFullStory);
            readFullStory.setVisibility(View.GONE);

            View storyModeration = itemView.findViewById(R.id.storyModeration);
            storyModeration.setVisibility(View.VISIBLE);

            Button publish = (Button) itemView.findViewById(R.id.publish);
            publish.setOnClickListener(onApproveClickListener);

            Button disapprove = (Button) itemView.findViewById(R.id.disapprove);
            disapprove.setOnClickListener(onDisapproveClickListener);
        }

        @Override
        public void bind(Story story) {
            this.story = story;
            super.bind(story);
        }

        private View.OnClickListener onApproveClickListener = view -> {
            if (storyModerationListener != null) {
                storyModerationListener.onApprove(story);
            }
        };

        private View.OnClickListener onDisapproveClickListener = view -> {
            if (storyModerationListener != null) {
                storyModerationListener.onDisapprove(story);
            }
        };
    }

    public List<News> getNews() {
        return news;
    }

    public interface StoryModerationListener {
        void onApprove(Story story);
        void onDisapprove(Story story);
    }

    public interface OnStoryViewListener {
        void onStoryViewClick(Story story);
    }

    public interface OnNewsViewListener {
        void onNewsViewClick(News news, Pair<View, String>... views);
    }

    public interface OnShareNewsListener {
        void onShareNews(News news);
    }

}
