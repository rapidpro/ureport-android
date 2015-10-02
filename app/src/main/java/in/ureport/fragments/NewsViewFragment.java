package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.managers.PrototypeManager;
import in.ureport.models.News;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class NewsViewFragment extends Fragment {

    private static final String EXTRA_NEWS = "news";

    private News news;

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

        TextView author = (TextView) view.findViewById(R.id.author);
        author.setText(String.format(getString(R.string.stories_list_item_author), news.getTags()));

        ImageView cover = (ImageView) view.findViewById(R.id.cover);
        if(news.getImages() != null && !news.getImages().isEmpty()) {
            ImageLoader.loadGenericPictureToImageViewFit(cover, news.getImages().get(0));
        }

        FloatingActionButton share = (FloatingActionButton) view.findViewById(R.id.share);
        share.setOnClickListener(onShareClickListener);
    }

    private View.OnClickListener onShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PrototypeManager.showPrototypeAlert(getActivity());
        }
    };
}
