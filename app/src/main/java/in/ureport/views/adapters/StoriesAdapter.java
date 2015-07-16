package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.models.Story;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

    private List<Story> stories;

    public StoriesAdapter(List<Story> stories) {
        this.stories = stories;
    }

    @Override
    public StoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_story, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoriesAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bind(stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        String authorTemplate;
        String contributionsTemplate;

        ImageView image;
        TextView title;
        TextView author;
        TextView markers;
        TextView contributions;

        public ViewHolder(View itemView) {
            super(itemView);

            authorTemplate = itemView.getContext().getString(R.string.stories_list_item_author);
            contributionsTemplate = itemView.getContext().getString(R.string.stories_list_item_contributions);

            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            markers = (TextView) itemView.findViewById(R.id.markers);
            contributions = (TextView) itemView.findViewById(R.id.contributions);
        }

        private void bind(Story story) {
            markers.setText(story.getMarkers());
            title.setText(story.getTitle());
            author.setText(String.format(authorTemplate, story.getUser().getUsername()));
            contributions.setText(String.format(contributionsTemplate, story.getContributions()));
        }
    }
}
