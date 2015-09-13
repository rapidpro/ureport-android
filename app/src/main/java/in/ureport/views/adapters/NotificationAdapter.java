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
import in.ureport.helpers.ImageLoader;
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

        private Notification notification;

        public ViewHolder(View itemView) {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.message);
            date = (TextView) itemView.findViewById(R.id.date);
            picture = (ImageView) itemView.findViewById(R.id.picture);
        }

        private void bindView(final Notification notification) {
            this.notification = notification;

            itemView.setOnClickListener(onNotificationItemClickListener);
            message.setText(notification.getMessage());
            date.setText(hourFormatter.format(notification.getDate()));

            if(notification.getUser() != null) {
                picture.setVisibility(View.VISIBLE);
                ImageLoader.loadPersonPictureToImageView(picture, notification.getUser().getPicture());
            } else {
                picture.setVisibility(View.GONE);
            }
        }

        private View.OnClickListener onNotificationItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.getOnNotificationSelectedListener() != null) {
                    notification.getOnNotificationSelectedListener().onNotificationSelected(notification);
                }
            }
        };
    }
}
