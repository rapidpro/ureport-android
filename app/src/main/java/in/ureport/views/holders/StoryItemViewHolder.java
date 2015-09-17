package in.ureport.views.holders;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.models.Story;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 17/09/15.
 */
public class StoryItemViewHolder extends RecyclerView.ViewHolder {

    private final String contributionsTemplate;

    private final StoriesAdapter.OnStoryViewListener onStoryViewListener;

    private final ImageView picture;
    private final ImageView image;
    private final TextView title;
    private final TextView author;
    private final TextView markers;
    private final TextView contributions;
    private final TextView summary;
    private final Button readFullStory;

    private Story story;

    public StoryItemViewHolder(View itemView, StoriesAdapter.OnStoryViewListener onStoryViewListener) {
        super(itemView);
        this.onStoryViewListener = onStoryViewListener;

        contributionsTemplate = itemView.getContext().getString(R.string.stories_list_item_contributions);

        picture = (ImageView) itemView.findViewById(R.id.picture);
        image = (ImageView) itemView.findViewById(R.id.image);
        title = (TextView) itemView.findViewById(R.id.title);
        author = (TextView) itemView.findViewById(R.id.author);
        markers = (TextView) itemView.findViewById(R.id.markers);
        contributions = (TextView) itemView.findViewById(R.id.contributors);
        summary = (TextView) itemView.findViewById(R.id.summary);

        readFullStory = (Button) itemView.findViewById(R.id.readFullStory);
        readFullStory.setOnClickListener(onReadFullStoryClickListener);
        itemView.setOnClickListener(onReadFullStoryClickListener);
    }

    public void bindInfo(@StringRes int info) {
        readFullStory.setText(info);
    }

    public void bind(Story story) {
        this.story = story;
        bindAuthor(story);
        bindImage(story);
        bindMarkers(story);

        summary.setText(story.getContent());
        title.setText(story.getTitle());
        contributions.setText(String.format(contributionsTemplate, story.getContributions()));
    }

    private void bindMarkers(Story story) {
        if(story.getMarkers() != null && story.getMarkers().length() > 0) {
            markers.setText(story.getMarkers());
            markers.setVisibility(View.VISIBLE);
        } else {
            markers.setVisibility(View.GONE);
        }
    }

    private void bindImage(Story story) {
        if(story.getCover() != null) {
            ImageLoader.loadGenericPictureToImageView(image, story.getCover());
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.GONE);
        }
    }

    private void bindAuthor(Story story) {
        if(story.getUserObject() != null) {
            ImageLoader.loadPersonPictureToImageView(picture, story.getUserObject().getPicture());
            author.setText(story.getUserObject().getNickname());
        }
    }

    private View.OnClickListener onReadFullStoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onStoryViewListener != null)
                onStoryViewListener.onStoryViewClick(story);
        }
    };
}
