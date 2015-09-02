package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

import in.ureport.R;
import in.ureport.managers.ImageLoader;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MediaAdapter";

    private static final int MEDIA_TYPE = 0;
    private static final int ADD_MEDIA_TYPE = 1;

    private static final long ADD_MEDIA_ITEM_ID = 1000;

    private List<Media> mediaList;
    private Media selectedMedia;

    private boolean editMode;

    private MediaListener mediaListener;

    public MediaAdapter(List<Media> mediaList, boolean editMode) {
        this.mediaList = mediaList;
        this.editMode = editMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case MEDIA_TYPE:
                return new MediaViewHolder(inflater.inflate(R.layout.view_media, parent, false));
            case ADD_MEDIA_TYPE:
                return new AddMediaViewHolder(inflater.inflate(R.layout.view_add_media, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case MEDIA_TYPE:
                ((MediaViewHolder)holder).bindView(mediaList.get(getCorrectPosition(position)));
        }
    }

    private int getCorrectPosition(int position) {
        return editMode ? position-1 : position;
    }

    @Override
    public long getItemId(int position) {
        if(getItemViewType(position) == ADD_MEDIA_TYPE) {
            return ADD_MEDIA_ITEM_ID;
        }
        return mediaList.get(position-1).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        if(editMode && position == 0)
            return ADD_MEDIA_TYPE;
        return MEDIA_TYPE;
    }

    @Override
    public int getItemCount() {
        if(editMode)
            return mediaList.size() + 1;
        else
            return mediaList.size();
    }

    public void setMediaListener(MediaListener mediaListener) {
        this.mediaListener = mediaListener;
    }

    public void updateMediaList(List<Media> mediaList) {
        selectFirstMediaIfNeeded(mediaList);

        this.mediaList = mediaList;
        notifyDataSetChanged();
    }

    private void selectFirstMediaIfNeeded(List<Media> mediaList) {
        if(mediaList != null && mediaList.size() > 0 && selectedMedia == null) {
            selectedMedia = mediaList.get(0);
        }
    }

    private class MediaViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;

        private final View cover;
        private final View coverUnselected;

        public MediaViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(onItemViewClickListener);

            image = (ImageView) itemView.findViewById(R.id.image);
            cover = itemView.findViewById(R.id.cover);
            coverUnselected = itemView.findViewById(R.id.coverUnselected);

            Button remove = (Button) itemView.findViewById(R.id.remove);
            remove.setVisibility(editMode ? View.VISIBLE : View.GONE);
            remove.setOnClickListener(onRemoveClickListener);
        }

        private void bindView(Media media) {
            bindCoverSelection(media);

            if (media.getType() == Media.Type.Video) {
                image.setImageResource(R.drawable.ic_video_grey600_36dp);
            } else {
                bindImage(media);
            }
        }

        private void bindCoverSelection(Media media) {
            coverUnselected.setVisibility(!isMediaSelected(media) ? View.VISIBLE : View.GONE);
            cover.setVisibility(isMediaSelected(media) ? View.VISIBLE : View.GONE);
        }

        private boolean isMediaSelected(Media media) {
            return selectedMedia != null && media.equals(selectedMedia);
        }

        private void bindImage(Media media) {
            if(media instanceof LocalMedia) {
                LocalMedia localMedia = (LocalMedia) media;
                image.setImageURI(localMedia.getPath());
            } else {
                ImageLoader.loadPersonPictureToImageView(image, media.getUrl());
            }
        }

        private View.OnClickListener onItemViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Media selectedMedia = MediaAdapter.this.selectedMedia;

                MediaAdapter.this.selectedMedia = mediaList.get(getCorrectPosition(getLayoutPosition()));
                notifyItemChanged(getLayoutPosition());

                if(selectedMedia != null && mediaList.indexOf(selectedMedia) >= 0) {
                    notifyItemChanged(mediaList.indexOf(selectedMedia)+1);
                }
            }
        };

        private View.OnClickListener onRemoveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaListener != null)
                    mediaListener.onMediaRemoveListener(getCorrectPosition(getLayoutPosition()));
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

    public Media getSelectedMedia() {
        return selectedMedia;
    }

    public interface MediaListener {
        void onMediaRemoveListener(int position);
        void onMediaAddListener();
    }
}
