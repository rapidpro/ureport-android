package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.models.News;
import in.ureport.models.Story;
import in.ureport.util.SpaceItemDecoration;
import in.ureport.util.WrapLinearLayoutManager;
import in.ureport.views.adapters.ContributionAdapter;
import in.ureport.views.adapters.MediaAdapter;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class NewsViewFragment extends Fragment {

    private static final String EXTRA_NEWS = "news";

    private News news;

    public static NewsViewFragment newInstance(News news) {
        NewsViewFragment stonewsViewFragmentyViewFragment = new NewsViewFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_NEWS, news);
        stonewsViewFragmentyViewFragment.setArguments(args);

        return stonewsViewFragmentyViewFragment;
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

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(news.getTitle());

        TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(news.getContent());

        TextView author = (TextView) view.findViewById(R.id.author);
        author.setText(String.format(getString(R.string.stories_list_item_author), news.getAuthor()));

        ImageView cover = (ImageView) view.findViewById(R.id.cover);
        cover.setImageResource(news.getCover());
    }
}
