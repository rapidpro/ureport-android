package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

import in.ureport.R;
import in.ureport.fragments.CreateStoryFragment;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MediaAdapter";

    private static final int MEDIA_TYPE = 0;
    private static final int ADD_MEDIA_TYPE = 1;

    private static final long ADD_MEDIA_ITEM_ID = 1000;

    private List<String> mediaList;
    private MediaListener mediaListener;

    public MediaAdapter(List<String> mediaList) {
        this.mediaList = mediaList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case MEDIA_TYPE:
                return new MediaViewHolder(inflater.inflate(R.layout.view_media, null));
            case ADD_MEDIA_TYPE:
                return new AddMediaViewHolder(inflater.inflate(R.layout.view_add_media, null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case MEDIA_TYPE:
                ((MediaViewHolder)holder).bindView(mediaList.get(position));
        }
    }

    @Override
    public long getItemId(int position) {
        if(getItemViewType(position) == ADD_MEDIA_TYPE) {
            return ADD_MEDIA_ITEM_ID;
        }
        String id = mediaList.get(position) + position;
        return id.hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mediaList.size()) {
            return ADD_MEDIA_TYPE;
        }

        return MEDIA_TYPE;
    }

    @Override
    public int getItemCount() {
        return mediaList.size() + 1;
    }

    public void setMediaListener(MediaListener mediaListener) {
        this.mediaListener = mediaListener;
    }

    public void updateMediaList(List<String> mediaList) {
        this.mediaList = mediaList;
        notifyDataSetChanged();
    }

    private class MediaViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public MediaViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);

            Button remove = (Button) itemView.findViewById(R.id.remove);
            remove.setOnClickListener(onRemoveClickListener);
        }

        private void bindView(String media) {
            if (media.equals(CreateStoryFragment.MEDIA_VIDEO)) {
                image.setImageResource(R.drawable.ic_video_grey600_36dp);
            } else {
                image.setImageResource(R.drawable.ic_camera_grey600_36dp);
            }
        }

        private View.OnClickListener onRemoveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaListener != null)
                    mediaListener.onMediaRemoveListener(getLayoutPosition());
            }
        };
    }

    private class AddMediaViewHolder extends RecyclerView.ViewHolder {
        public AddMediaViewHolder(View itemView) {
            super(itemView);

            View addMedia = itemView.findViewById(R.id.addMedia);
            addMedia.setOnClickListener(onAddMediaClickListener);
        }

        private View.OnClickListener onAddMediaClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaListener != null)
                    mediaListener.onMediaAddListener();
            }
        };
    }

    public static interface MediaListener {
        void onMediaRemoveListener(int position);
        void onMediaAddListener();
    }
}
