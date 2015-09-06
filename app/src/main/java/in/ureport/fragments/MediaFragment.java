package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.models.Media;

/**
 * Created by johncordeiro on 04/09/15.
 */
public class MediaFragment extends Fragment {

    private static final String EXTRA_MEDIA = "media";
    private Media media;

    private MediaViewFragment.OnCloseMediaViewListener onCloseMediaViewListener;

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
                onCloseMediaViewListener.onCloseMediaView();
            }
        });
        setupView(view);
    }

    private void setupView(View view) {
        ImageView image = (ImageView) view.findViewById(R.id.image);
        ImageLoader.loadPictureToImageView(image, media);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MediaViewFragment.OnCloseMediaViewListener) {
            onCloseMediaViewListener = (MediaViewFragment.OnCloseMediaViewListener) context;
        }
    }
}
