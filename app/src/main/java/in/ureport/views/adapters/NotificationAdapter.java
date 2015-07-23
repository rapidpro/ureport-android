package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import in.ureport.R;
import in.ureport.managers.UserDataManager;
import in.ureport.models.Notification;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class NotificationAdapter extends RecyclerView.Adapter {

    private List<Notification> notifications;
    private DateFormat hourFormatter;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
        this.hourFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bindView(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView message;
        private final TextView date;
        private final ImageView picture;

        public ViewHolder(View itemView) {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.message);
            date = (TextView) itemView.findViewById(R.id.date);
            picture = (ImageView) itemView.findViewById(R.id.picture);
        }

        private void bindView(Notification notification) {
            message.setText(notification.getMessage());
            date.setText(hourFormatter.format(notification.getDate()));

            if(notification.getUser() != null) {
                picture.setVisibility(View.VISIBLE);
                picture.setImageResource(UserDataManager.getUserImage(itemView.getContext(), notification.getUser()));
            } else {
                picture.setVisibility(View.GONE);
            }
        }
    }
}
