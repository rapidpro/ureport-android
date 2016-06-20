package in.ureport.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.ilhasoft.support.tool.bitmap.BitmapLoader;
import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.helpers.TimeFormatter;
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
    private OnMediaViewListener onMediaViewListener;

    private BitmapLoader bitmapLoader;

    public MediaAdapter(List<Media> mediaList, boolean editMode) {
        this.mediaList = mediaList;
        this.editMode = editMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        setupObjects(parent);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case MEDIA_TYPE:
                return new MediaViewHolder(inflater.inflate(R.layout.view_media, parent, false));
            case ADD_MEDIA_TYPE:
                return new AddMediaViewHolder(inflater.inflate(R.layout.view_add_media, parent, false));
        }
        return null;
    }

    private void setupObjects(ViewGroup parent) {
        if(bitmapLoader == null) {
            bitmapLoader = new BitmapLoader(parent.getContext());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case MEDIA_TYPE:
                MediaViewHolder mediaViewHolder = ((MediaViewHolder)holder);
                mediaViewHolder.bindView(mediaList.get(getCorrectPosition(position)));
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

    public void setOnMediaViewListener(OnMediaViewListener onMediaViewListener) {
        this.onMediaViewListener = onMediaViewListener;
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
            for (int i = 0; i < mediaList.size(); i++) {
                Media media = mediaList.get(i);
                if(isMediaSelectable(media)) {
                    selectedMedia = media;
                    break;
                }
            }
        }
    }

    private boolean isMediaSelectable(Media media) {
        return media.getType() != Media.Type.File && media.getType() != Media.Type.Audio;
    }

    private class MediaViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final View cover;
        private final ImageView videoPlay;
        private final TextView name;

        public MediaViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.image);
            cover = itemView.findViewById(R.id.cover);
            videoPlay = (ImageView) itemView.findViewById(R.id.videoPlay);

            Button remove = (Button) itemView.findViewById(R.id.remove);
            remove.setVisibility(editMode ? View.VISIBLE : View.GONE);
            remove.setOnClickListener(editMode ? onRemoveClickListener : null);
            itemView.setOnClickListener(editMode ? onItemSelectClickListener : onItemViewClickListener);
        }

        private void bindView(Media media) {
            bindCoverSelection(media);
            bindImage(media);
        }

        private void bindCoverSelection(Media media) {
            if(editMode) {
                cover.setVisibility(isMediaSelected(media) ? View.VISIBLE : View.INVISIBLE);
            } else {
                cover.setVisibility(View.INVISIBLE);
                videoPlay.setVisibility(isVideoType(media) ? View.VISIBLE : View.GONE);
            }
        }

        private boolean isVideoType(Media media) {
            return media.getType() == Media.Type.Video || media.getType() == Media.Type.VideoPhone;
        }

        private boolean isMediaSelected(Media media) {
            return selectedMedia != null && media.equals(selectedMedia);
        }

        private void bindImage(Media media) {
            resetDefaults();

            if(media instanceof LocalMedia) {
                bindLocalImage((LocalMedia) media);
            } else {
                bindRemoteImage(media);
            }
        }

        private void bindRemoteImage(Media media) {
            switch (media.getType()) {
                case File:
                    bindFile(media);
                    break;
                case Audio:
                    bindAudio(media);
                    break;
                default:
                    ImageLoader.loadGenericPictureToImageViewFit(image, getCoverUrl(media));
            }
        }

        private void bindLocalImage(LocalMedia media) {
            switch (media.getType()) {
                case VideoPhone:
                    bitmapLoader.loadBitmapByVideoPath(media.getPath(), image, 100); break;
                case File:
                    bindFile(media);
                    break;
                case Audio:
                    bindAudio(media);
                    break;
                default:
                    bitmapLoader.loadBitmapByUri(media.getPath(), image, 100);
            }
        }

        private void bindFile(Media media) {
            if(containsMetadata(media, Media.KEY_FILENAME)) {
                String filename = (String)media.getMetadata().get(Media.KEY_FILENAME);
                name.setText(filename);
            }
            bindGeneric(R.drawable.ic_folder_white_24dp, R.color.orange);
        }

        private void bindAudio(Media media) {
            if(containsMetadata(media, Media.KEY_DURATION)) {
                int duration = (Integer)media.getMetadata().get(Media.KEY_DURATION);
                name.setText(TimeFormatter.getDurationString(duration));
                name.setGravity(Gravity.END | Gravity.RIGHT);
            }
            bindGeneric(R.drawable.ic_music_note_white_24dp, R.color.light_green_highlight);
        }

        private boolean containsMetadata(Media media, String metadataKey) {
            return media.getMetadata() != null && media.getMetadata().containsKey(metadataKey);
        }

        private void bindGeneric(int imageResource, int color) {
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            image.setImageResource(imageResource);
            image.setBackgroundColor(itemView.getResources().getColor(color));
        }

        private void resetDefaults() {
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setBackgroundColor(itemView.getResources().getColor(android.R.color.transparent));
            name.setGravity(Gravity.CENTER_HORIZONTAL);
            name.setText(null);
        }

        private String getCoverUrl(Media media) {
            switch (media.getType()) {
                case VideoPhone:
                    return media.getThumbnail();
                default:
                    return media.getUrl();
            }
        }

        private View.OnClickListener onItemViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMediaViewListener != null) {
                    Media media = mediaList.get(getLayoutPosition());
                    switch (media.getType()) {
                        case File:
                            onMediaViewListener.onFileMediaView(media); break;
                        case Audio:
                            onMediaViewListener.onAudioMediaView(media); break;
                        case VideoPhone:
                            onMediaViewListener.onVideoMediaView(media); break;
                        default:
                            onMediaViewListener.onMediaView(mediaList, getLayoutPosition());
                    }
                }
            }
        };

        private View.OnClickListener onItemSelectClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Media currentSelectedMedia = MediaAdapter.this.selectedMedia;
                Media newSelectedMedia = mediaList.get(getCorrectPosition(getLayoutPosition()));

                if(isMediaSelectable(newSelectedMedia)) {
                    MediaAdapter.this.selectedMedia = newSelectedMedia;
                    notifyItemChanged(getLayoutPosition());

                    if (currentSelectedMedia != null && mediaList.indexOf(currentSelectedMedia) >= 0) {
                        notifyItemChanged(mediaList.indexOf(currentSelectedMedia) + 1);
                    }
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

    public void setSelectedMedia(Media selectedMedia) {
        this.selectedMedia = selectedMedia;
        notifyDataSetChanged();
    }

    public Media getSelectedMedia() {
        return selectedMedia;
    }

    public interface MediaListener {
        void onMediaRemoveListener(int position);
        void onMediaAddListener();
    }

    public interface OnMediaViewListener {
        void onMediaView(List<Media> medias, int position);
        void onVideoMediaView(Media media);
        void onFileMediaView(Media media);
        void onAudioMediaView(Media media);
    }
}
