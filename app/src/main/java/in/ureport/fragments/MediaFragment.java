package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.models.Media;
import in.ureport.views.widgets.TouchImageView;

/**
 * Created by johncordeiro on 04/09/15.
 */
public class MediaFragment extends Fragment {

    private static final String EXTRA_MEDIA = "media";
    private Media media;

    private MediaViewFragment.OnCloseMediaViewListener onCloseMediaViewListener;
    private TouchImageView image;

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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCloseMediaViewListener != null)
                    onCloseMediaViewListener.onCloseMediaView();
            }
        });
        setupView(view);
    }

    private void setupView(View view) {
        image = (TouchImageView) view.findViewById(R.id.image);
        ImageLoader.loadPictureToImageView(image, media, onImageLoadedCallback);
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
}
