package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.helpers.SpaceItemDecoration;
import in.ureport.models.Media;
import in.ureport.models.News;
import in.ureport.tasks.ShareNewsTask;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class NewsViewFragment extends Fragment {

    private static final int START_OFFSET_FAB_ANIMATION = 500;

    private static final String EXTRA_NEWS = "news";

    private News news;

    private MediaAdapter.OnMediaViewListener onMediaViewListener;

    private FloatingActionButton share;

    public static NewsViewFragment newInstance(News news) {
        NewsViewFragment newsViewFragment = new NewsViewFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_NEWS, news);
        newsViewFragment.setArguments(args);

        return newsViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_NEWS)) {
            news = getArguments().getParcelable(EXTRA_NEWS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof MediaAdapter.OnMediaViewListener) {
            onMediaViewListener = (MediaAdapter.OnMediaViewListener) context;
        }
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.setTitle("");

        TextView category = (TextView) view.findViewById(R.id.category);
        if(news.getCategory() != null && news.getCategory().getName() != null) {
            category.setText(news.getCategory().getName());
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(news.getTitle());

        TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(news.getSummary());

        TextView author = (TextView) view.findViewById(R.id.tags);
        author.setText(news.getTags());

        ImageView cover = (ImageView) view.findViewById(R.id.cover);
        if(hasImages()) {
            ImageLoader.loadGenericPictureToImageViewFit(cover, news.getImages().get(0));
        }

        share = (FloatingActionButton) view.findViewById(R.id.share);
        share.setOnClickListener(onShareClickListener);
        showFloatingButton();

        setupMediaList(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideFloatingButton();
    }

    private void hideFloatingButton() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_floating_button_in);
        animation.setInterpolator(new OvershootInterpolator());
        share.startAnimation(animation);
    }

    private void showFloatingButton() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_floating_button_in);
        animation.setStartOffset(START_OFFSET_FAB_ANIMATION);
        animation.setInterpolator(new OvershootInterpolator());
        share.startAnimation(animation);
    }

    private boolean hasImages() {
        return news.getImages() != null && !news.getImages().isEmpty();
    }

    private void setupMediaList(View view) {
        RecyclerView mediaList = (RecyclerView) view.findViewById(R.id.mediaList);
        if(hasImages()) {
            mediaList.setVisibility(View.VISIBLE);
            mediaList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

            UnitConverter converter = new UnitConverter(getActivity());

            SpaceItemDecoration mediaItemDecoration = new SpaceItemDecoration();
            mediaItemDecoration.setHorizontalSpaceWidth((int) converter.convertDpToPx(10));
            mediaList.addItemDecoration(mediaItemDecoration);

            MediaAdapter adapter = new MediaAdapter(getMedias(), false);
            adapter.setOnMediaViewListener(onMediaViewListener);
            mediaList.setAdapter(adapter);
        }
    }

    @NonNull
    private List<Media> getMedias() {
        List<Media> medias = new ArrayList<>();
        for (String image : news.getImages()) {
            medias.add(new Media(null, image, Media.Type.Picture, null));
        }
        return medias;
    }

    private View.OnClickListener onShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShareNewsTask shareNewsTask = new ShareNewsTask(NewsViewFragment.this, news);
            shareNewsTask.execute();
        }
    };
}
