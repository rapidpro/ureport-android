package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
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
        private final TextView points;
        private final ImageView picture;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            picture = (ImageView) itemView.findViewById(R.id.picture);
            points = (TextView) itemView.findViewById(R.id.points);
        }

        private void bindView(User user) {
            name.setText(user.getNickname());
            points.setText(String.valueOf(getPoints(user)));
            ImageLoader.loadPersonPictureToImageView(picture, user.getPicture());
        }

        private Integer getPoints(User user) {
            return user.getPoints() != null ? user.getPoints() : 0;
        }
    }
}
