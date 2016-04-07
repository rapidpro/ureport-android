package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.helpers.YoutubePlayer;
import in.ureport.models.Media;
import in.ureport.views.widgets.TouchImageView;

/**
 * Created by johncordeiro on 04/09/15.
 */
public class MediaFragment extends Fragment {

    private static final String TAG = "MediaFragment";

    private static final String EXTRA_MEDIA = "media";
    private Media media;

    private MediaViewFragment.OnCloseMediaViewListener onCloseMediaViewListener;

    private TouchImageView image;
    private TextView videoPlay;

    public static MediaFragment newInstance(Media media) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MEDIA, media);
        
        MediaFragment fragment = new MediaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(EXTRA_MEDIA)) {
            media = getArguments().getParcelable(EXTRA_MEDIA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        image = (TouchImageView) view.findViewById(R.id.image);
        ImageLoader.loadPictureToImageView(image, media, onImageLoadedCallback);

        videoPlay = (TextView) view.findViewById(R.id.videoPlay);
        bindVideo();
    }

    private void bindVideo() {
        if(media != null) {
            switch (media.getType()) {
                case Video:
                    image.setEnabled(false);
                    videoPlay.setVisibility(View.VISIBLE);
                    videoPlay.setOnClickListener(onVideoPlayClickListener);
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MediaViewFragment.OnCloseMediaViewListener) {
            onCloseMediaViewListener = (MediaViewFragment.OnCloseMediaViewListener) context;
        }
    }

    private Callback onImageLoadedCallback = new Callback() {
        @Override
        public void onSuccess() {
            image.setZoom(image.getCurrentZoom());
        }

        @Override
        public void onError() {}
    };

    private View.OnClickListener onVideoPlayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(media.getType() == Media.Type.Video) {
                YoutubePlayer youtubePlayer = new YoutubePlayer(getActivity());
                youtubePlayer.playVideoMedia(media);
            }
        }
    };

}
