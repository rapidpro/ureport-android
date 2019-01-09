package in.ureport.views.holders;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;

import br.com.ilhasoft.support.tool.DateFormatter;
import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.listener.OnNeedUpdateStoryListener;
import in.ureport.listener.OnUserStartChattingListener;
import in.ureport.managers.UserViewManager;
import in.ureport.models.Media;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.models.holders.StoryHolder;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 17/09/15.
 */
public class StoryItemViewHolder extends RecyclerView.ViewHolder {

    private final String contributionsTemplate;

    private final StoriesAdapter.OnStoryViewListener onStoryViewListener;
    private final OnUserStartChattingListener onUserStartChattingListener;
    private final OnNeedUpdateStoryListener onNeedUpdateStoryListener;

    private final TextView publishedDate;
    private final ImageView picture;
    private final ImageView image;
    private final TextView title;
    private final TextView author;
    private final TextView markers;
    private final TextView filesAttached;
    private final TextView contributions;
    private final TextView likeCountText;
    private final TextView summary;
    private final Button readFullStory;

    private Story story;

    private UserViewManager userViewManager;

    public StoryItemViewHolder(View itemView, StoriesAdapter.OnStoryViewListener onStoryViewListener
            , OnUserStartChattingListener onUserStartChattingListener, OnNeedUpdateStoryListener onNeedUpdateStoryListener) {
        super(itemView);
        this.onStoryViewListener = onStoryViewListener;
        this.onUserStartChattingListener = onUserStartChattingListener;
        this.onNeedUpdateStoryListener = onNeedUpdateStoryListener;

        contributionsTemplate = itemView.getContext().getString(R.string.stories_list_item_contributions);

        publishedDate = (TextView) itemView.findViewById(R.id.publishedDate);
        picture = (ImageView) itemView.findViewById(R.id.picture);
        image = (ImageView) itemView.findViewById(R.id.image);
        title = (TextView) itemView.findViewById(R.id.title);
        author = (TextView) itemView.findViewById(R.id.tags);
        markers = (TextView) itemView.findViewById(R.id.markers);
        filesAttached = (TextView) itemView.findViewById(R.id.filesAttached);
        contributions = (TextView) itemView.findViewById(R.id.contributors);
        likeCountText = (TextView) itemView.findViewById(R.id.likeCountText);
        summary = (TextView) itemView.findViewById(R.id.summary);

        readFullStory = (Button) itemView.findViewById(R.id.readFullStory);
        readFullStory.setOnClickListener(onReadFullStoryClickListener);
        itemView.setOnClickListener(onReadFullStoryClickListener);

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
        bindImage(story);
        bindMarkers(story);
        bindFilesAttached(story);

        summary.setText(story.getContent());
        title.setText(story.getTitle());

        picture.setOnClickListener(onUserClickListener);
        author.setOnClickListener(onUserClickListener);
    }

    private void bindPublishDate(Story story) {
        DateFormatter dateFormatter;
        dateFormatter = new DateFormatter();

        String timeElapsed = dateFormatter.getTimeElapsed(story.getCreatedDate().getTime()
                , itemView.getContext().getString(R.string.date_now));
        this.publishedDate.setText(timeElapsed.toLowerCase());
    }

    private void bindContributions(Integer contributionsCount) {
        if (contributionsCount != null && contributionsCount > 0) {
            contributions.setText(String.format(contributionsTemplate, contributionsCount));
        } else {
            contributions.setText(String.format(contributionsTemplate, 0));
        }
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

    private void bindLikes(Integer likeCount) {
        if (likeCount != null && likeCount > 0) {
            String likes = itemView.getResources().getQuantityString(R.plurals.like_count
                    , story.getLikes(), story.getLikes());
            likeCountText.setText(likes);
            likeCountText.setVisibility(View.VISIBLE);
        } else {
            likeCountText.setVisibility(View.GONE);
        }
    }

    private void bindFilesAttached(Story story) {
        if (story.getMedias() != null && story.getMedias().size() > 0) {
            String filesAttachedText = itemView.getResources().getQuantityString(R.plurals.files_attached
                    , story.getMedias().size(), story.getMedias().size());
            filesAttached.setText(filesAttachedText);
            filesAttached.setVisibility(View.VISIBLE);
        } else {
            filesAttached.setVisibility(View.GONE);
        }
    }

    private void bindMarkers(Story story) {
        if (story.getMarkers() != null && story.getMarkers().length() > 0) {
            markers.setText(story.getMarkers());
            markers.setVisibility(View.VISIBLE);
        } else {
            markers.setVisibility(View.GONE);
        }
    }

    private void bindImage(Story story) {
        Media cover = story.getCover();
        if (cover != null) {
            image.setBackgroundColor(itemView.getResources().getColor(android.R.color.transparent));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setVisibility(View.VISIBLE);

            switch (cover.getType()) {
                case Video:
                case VideoPhone:
                case Picture:
                    ImageLoader.loadGenericPictureToImageViewFit(image, getCoverUrl(story));
                    break;
                default:
                    image.setVisibility(View.GONE);
            }
        } else {
            image.setVisibility(View.GONE);
        }
    }

    private String getCoverUrl(Story story) {
        switch (story.getCover().getType()) {
            case VideoPhone:
                return story.getCover().getThumbnail();
            default:
                return story.getCover().getUrl();
        }
    }

    private void bindAuthor(User user) {
        if (user != null) {
            ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());
            author.setText(user.getNickname());
        } else {
            picture.setImageResource(R.drawable.face);
            author.setText("");
        }
    }

    private View.OnClickListener onReadFullStoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onStoryViewListener != null) {
                onStoryViewListener.onStoryViewClick(story, getViewsTransitions());
            }
        }

        @NonNull
        private Pair<View, String>[] getViewsTransitions() {
            Pair<View, String> picturePair = Pair.create((View) picture
                    , itemView.getContext().getString(R.string.transition_profile_picture));

            Pair<View, String> nicknamePair = Pair.create((View) author
                    , itemView.getContext().getString(R.string.transition_profile_nickname));

            Pair<View, String> storyTitle = Pair.create((View) title
                    , itemView.getContext().getString(R.string.transition_story_title));

            Pair<View, String>[] views = (Pair<View, String>[]) Array.newInstance(Pair.class, 3);
            views[0] = picturePair;
            views[1] = nicknamePair;
            views[2] = storyTitle;
            return views;
        }
    };

    private View.OnClickListener onUserClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            userViewManager.showUserInfo(story.getUserObject(), onUserStartChattingListener);
        }
    };
}
