package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 18/07/15.
 */
public class RankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> users;

    public RankingAdapter(List<User> users) {
        this.users = users;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_ranking, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView firstLetter;
        private final TextView polls;
        private final TextView stories;
        private final TextView points;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            firstLetter = (TextView) itemView.findViewById(R.id.firstLetter);
            polls = (TextView) itemView.findViewById(R.id.polls);
            stories = (TextView) itemView.findViewById(R.id.stories);
            points = (TextView) itemView.findViewById(R.id.points);
        }

        private void bindView(User user) {
            name.setText("@"+user.getUsername());
            firstLetter.setText(user.getUsername().toUpperCase());
            polls.setText(itemView.getContext().getString(R.string.profile_polls, user.getPolls()));
            stories.setText(itemView.getContext().getString(R.string.profile_stories, user.getStories()));
            points.setText(String.valueOf(user.getPoints()));
        }
    }
}
