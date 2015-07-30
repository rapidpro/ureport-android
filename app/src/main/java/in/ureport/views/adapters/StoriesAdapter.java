package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.managers.UserViewManager;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private List<Story> stories;

    private boolean publicType = false;
    private User user;

    private OnStoryViewListener onStoryViewListener;
    private OnPublishStoryListener onPublishStoryListener;

    public StoriesAdapter(List<Story> stories) {
        this.stories = stories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch(type) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_story_header, viewGroup, false));
            default:
            case TYPE_ITEM:
                return new ItemViewHolder(inflater.inflate(R.layout.item_story, viewGroup, false));
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
                ((ItemViewHolder)viewHolder).bind(stories.get(getListPosition(position)));
        }
    }

    private int getListPosition(int position) {
        if(publicType) return position-1;
        return position;
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
            name.setHint(itemView.getContext().getString(R.string.list_stories_header_title, user.getUsername()));
            picture.setImageResource(UserViewManager.getUserImage(itemView.getContext(), user));
        }

        private View.OnClickListener onPublishStoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPublishStoryListener != null)
                    onPublishStoryListener.onPublishStory();
            }
        };
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final String contributionsTemplate;

        private final ImageView picture;
        private final ImageView image;
        private final TextView title;
        private final TextView author;
        private final TextView markers;
        private final TextView contributions;
        private final TextView summary;

        public ItemViewHolder(View itemView) {
            super(itemView);

            contributionsTemplate = itemView.getContext().getString(R.string.stories_list_item_contributions);

            picture = (ImageView) itemView.findViewById(R.id.picture);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            markers = (TextView) itemView.findViewById(R.id.markers);
            contributions = (TextView) itemView.findViewById(R.id.contributors);
            summary = (TextView) itemView.findViewById(R.id.summary);

            Button readFullStory = (Button) itemView.findViewById(R.id.readFullStory);
            readFullStory.setOnClickListener(onReadFullStoryClickListener);
            itemView.setOnClickListener(onReadFullStoryClickListener);
        }

        private void bind(Story story) {
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
            if(story.getImage() != null) {
                int imageId = itemView.getResources().getIdentifier(story.getImage(), "drawable"
                        , itemView.getContext().getPackageName());
                image.setImageResource(imageId);
                image.setVisibility(View.VISIBLE);
            } else {
                image.setVisibility(View.GONE);
            }
        }

        private void bindAuthor(Story story) {
            picture.setImageResource(UserViewManager.getUserImage(itemView.getContext(), story.getUser()));
            author.setText("@" + story.getUser().getUsername());
        }

        private View.OnClickListener onReadFullStoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onStoryViewListener != null)
                    onStoryViewListener.onStoryViewClick(stories.get(getListPosition(getLayoutPosition())));
            }
        };
    }

    public interface OnStoryViewListener {
        void onStoryViewClick(Story story);
    }

    public interface OnPublishStoryListener {
        void onPublishStory();
    }
}
