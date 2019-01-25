package in.ureport.views.holders;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.ilhasoft.support.tool.ColorHelper;
import br.com.ilhasoft.support.tool.DateFormatter;
import br.com.ilhasoft.support.tool.ResourceUtil;
import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.listener.OnNeedUpdateStoryListener;
import in.ureport.listener.OnUserStartChattingListener;
import in.ureport.managers.UserViewManager;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.models.holders.StoryHolder;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 17/09/15.
 */
public class StoryItemViewHolder extends RecyclerView.ViewHolder {

    private final StoriesAdapter.OnStoryViewListener onStoryViewListener;
    private final OnUserStartChattingListener onUserStartChattingListener;
    private final OnNeedUpdateStoryListener onNeedUpdateStoryListener;

    private final ImageView authorPicture;
    private final TextView authorName;
    private final TextView publishedDate;
    private final TextView title;
    private final TextView markers;
    private final TextView content;
    private final TextView contributionsCount;
    private final TextView likeCount;
    private final TextView readFullStory;

    private Story story;

    private UserViewManager userViewManager;

    public StoryItemViewHolder(View itemView, StoriesAdapter.OnStoryViewListener onStoryViewListener,
                               OnUserStartChattingListener onUserStartChattingListener,
                               OnNeedUpdateStoryListener onNeedUpdateStoryListener) {
        super(itemView);
        this.onStoryViewListener = onStoryViewListener;
        this.onUserStartChattingListener = onUserStartChattingListener;
        this.onNeedUpdateStoryListener = onNeedUpdateStoryListener;

        authorPicture = itemView.findViewById(R.id.authorPicture);
        authorName = itemView.findViewById(R.id.authorName);
        publishedDate = itemView.findViewById(R.id.publishedDate);
        title = itemView.findViewById(R.id.title);
        markers = itemView.findViewById(R.id.markers);
        content = itemView.findViewById(R.id.content);
        contributionsCount = itemView.findViewById(R.id.contributionsCount);
        likeCount = itemView.findViewById(R.id.likeCount);

        readFullStory = itemView.findViewById(R.id.readFullStory);
        readFullStory.setOnClickListener(onReadFullStoryClickListener);
        readFullStory.setTextColor(new ResourceUtil(itemView.getContext())
                .getColorByAttr(R.attr.colorPrimary));

        userViewManager = new UserViewManager(itemView.getContext());
    }

    public void bindInfo(@StringRes int info) {
        readFullStory.setText(info);
    }

    public void bind(Story story) {
        this.story = story;
        bindPublishDate(story);

        if (story.getUserObject() != null) {
            bindAuthor(story.getUserObject());
            bindLikes(story.getLikes());
            bindContributions(story.getContributions());
        } else if (onNeedUpdateStoryListener != null) {
            StoryHolder storyHolder = onNeedUpdateStoryListener.loadStoryData(story);
            bindStoryHolder(storyHolder);
        }
        bindMarkers(story);

        content.setText(story.getContent());
        title.setText(story.getTitle());

        authorPicture.setOnClickListener(onUserClickListener);
        authorName.setOnClickListener(onUserClickListener);
    }

    private void bindPublishDate(Story story) {
        DateFormatter dateFormatter;
        dateFormatter = new DateFormatter();

        String timeElapsed = dateFormatter.getTimeElapsed(story.getCreatedDate()
                , itemView.getContext().getString(R.string.date_now));
        this.publishedDate.setText(timeElapsed.toLowerCase());
    }

    private void bindStoryHolder(StoryHolder storyHolder) {
        if (storyHolder != null) {
            bindAuthor(storyHolder.getUserObject());
            bindLikes(storyHolder.getLikes());
            bindContributions(storyHolder.getContributions());
        } else {
            bindAuthor(null);
            bindLikes(null);
            bindContributions(null);
        }
    }

    private void bindLikes(Integer count) {
        likeCount.setText((count == null) ? "0" : String.valueOf(count));
    }

    private void bindContributions(Integer count) {
        contributionsCount.setText((count == null) ? "0" : String.valueOf(count));
    }

    private void bindMarkers(Story story) {
        if (story.getMarkers() != null && story.getMarkers().length() > 0) {
            markers.setText(story.getMarkers());
            markers.setVisibility(View.VISIBLE);
        } else {
            markers.setVisibility(View.GONE);
        }
    }

    private void bindAuthor(User user) {
        if (user != null) {
            ImageLoader.loadPersonPictureToImageView(authorPicture, user.getPicture());
            authorName.setText(user.getNickname());
        } else {
            authorPicture.setImageResource(R.drawable.face);
            authorName.setText("");
        }
    }

    private View.OnClickListener onReadFullStoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onStoryViewListener != null) {
                onStoryViewListener.onStoryViewClick(story);
            }
        }
    };

    private View.OnClickListener onUserClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            userViewManager.showUserInfo(story.getUserObject(), onUserStartChattingListener);
        }
    };

}
