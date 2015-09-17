package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.DepthPageTransformer;
import in.ureport.models.Media;
import in.ureport.views.adapters.MediaViewAdapter;

/**
 * Created by johncordeiro on 04/09/15.
 */
public class MediaViewFragment extends Fragment {

    private static final String TAG = "MediaViewFragment";

    private static final String EXTRA_MEDIAS = "medias";
    private static final String EXTRA_POSITION = "position";

    private List<Media> medias;
    private int position = 0;

    private ViewPager mediaPager;

    private OnCloseMediaViewListener onCloseMediaViewListener;
    private MediaViewAdapter mediaViewAdapter;

    public static MediaViewFragment newInstance(ArrayList<Media> medias, int position) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_MEDIAS, medias);
        args.putInt(EXTRA_POSITION, position);

        MediaViewFragment fragment = new MediaViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(EXTRA_MEDIAS)) {
            medias = getArguments().getParcelableArrayList(EXTRA_MEDIAS);
            position = getArguments().getInt(EXTRA_POSITION, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnCloseMediaViewListener) {
            onCloseMediaViewListener = (OnCloseMediaViewListener) context;
        }
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle("");

        mediaPager = (ViewPager) view.findViewById(R.id.mediaPager);
        mediaPager.addOnPageChangeListener(onPageChangeListener);

        mediaViewAdapter = new MediaViewAdapter(getChildFragmentManager(), medias);
        mediaPager.setAdapter(mediaViewAdapter);
        mediaPager.setCurrentItem(position);
        mediaPager.setPageTransformer(true, new DepthPageTransformer());
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            if (position == mediaViewAdapter.getCount() - 1) {
                closeMediaViewFragmentDelayed();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    private void closeMediaViewFragmentDelayed() {
        mediaPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                onCloseMediaViewListener.onCloseMediaView();
            }
        }, 500);
    }

    public interface OnCloseMediaViewListener {
        void onCloseMediaView();
    }
}
