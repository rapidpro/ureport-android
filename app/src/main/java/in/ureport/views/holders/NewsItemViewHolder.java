package in.ureport.views.holders;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.models.News;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 01/10/15.
 */
public class NewsItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView description;
    private final TextView category;
    private final Button share;
    private TextView title;
    private TextView tags;
    private ImageView cover;
    private View mediaLayer;

    private StoriesAdapter.OnNewsViewListener onNewsViewListener;
    private final StoriesAdapter.OnShareNewsListener onShareNewsListener;

    private News news;

    public NewsItemViewHolder(View itemView, StoriesAdapter.OnNewsViewListener onNewsViewListener
        , StoriesAdapter.OnShareNewsListener onShareNewsListener) {
        super(itemView);
        this.onNewsViewListener = onNewsViewListener;
        this.onShareNewsListener = onShareNewsListener;

        category = (TextView) itemView.findViewById(R.id.category);
        cover = (ImageView) itemView.findViewById(R.id.cover);
        mediaLayer = itemView.findViewById(R.id.mediaLayer);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        tags = (TextView) itemView.findViewById(R.id.tags);

        itemView.setOnClickListener(onNewsClickListener);

        share = (Button) itemView.findViewById(R.id.share);
        share.setOnClickListener(onShareClickListener);
    }

    public void bind(News news) {
        this.news = news;

        category.setText(news.getCategory().getName());
        title.setText(news.getTitle());
        description.setText(news.getSummary());
        tags.setText(news.getTags());

        if(news.getImages() != null && !news.getImages().isEmpty()) {
            ImageLoader.loadGenericPictureToImageViewFit(cover, news.getImages().get(0));
        }
    }

    public void prepareForShare() {
        share.setVisibility(View.GONE);
    }

    private View.OnClickListener onNewsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(onNewsViewListener != null) {
                onNewsViewListener.onNewsViewClick(news, getViewsTransitions());
            }
        }
    };

    @NonNull
    private Pair<View, String>[] getViewsTransitions() {
        Pair<View, String> picturePair = Pair.create((View)cover
                , itemView.getContext().getString(R.string.transition_media));

        Pair<View, String> storyTitlePair = Pair.create((View)title
                , itemView.getContext().getString(R.string.transition_story_title));

        Pair<View, String> mediaLayerPair = Pair.create(mediaLayer
                , itemView.getContext().getString(R.string.transition_media_layer));

        Pair<View, String> categoryPair = Pair.create((View)category
                , itemView.getContext().getString(R.string.transition_news_category));

        Pair<View, String> tagsPair = Pair.create((View)tags
                , itemView.getContext().getString(R.string.transition_tags));

        Pair<View, String> [] views = (Pair<View, String> []) Array.newInstance(Pair.class, 5);
        views[0] = picturePair;
        views[1] = storyTitlePair;
        views[2] = mediaLayerPair;
        views[3] = categoryPair;
        views[4] = tagsPair;
        return views;
    }

    private View.OnClickListener onShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(onShareNewsListener != null) {
                onShareNewsListener.onShareNews(news);
            }
        }
    };
}
