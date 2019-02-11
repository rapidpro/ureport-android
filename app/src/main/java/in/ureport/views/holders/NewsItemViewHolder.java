package in.ureport.views.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.News;
import in.ureport.views.adapters.StoriesAdapter;

/**
 * Created by johncordeiro on 01/10/15.
 */
public class NewsItemViewHolder extends RecyclerView.ViewHolder {

    private RoundedImageView cover;
    private TextView title;
    private TextView tags;
    private TextView description;
    private TextView seeFullNews;
//    private TextView category;

    private StoriesAdapter.OnNewsViewListener onNewsViewListener;

    private News news;

    public NewsItemViewHolder(View itemView, StoriesAdapter.OnNewsViewListener onNewsViewListener) {
        super(itemView);
        this.onNewsViewListener = onNewsViewListener;

        TextView authorName = itemView.findViewById(R.id.authorName);
        authorName.setText(itemView.getContext().getString(R.string.ureport_name,
                CountryProgramManager.getCurrentCountryProgram().getName()));

        cover = itemView.findViewById(R.id.cover);
        title = itemView.findViewById(R.id.title);
        tags = itemView.findViewById(R.id.markers);
        description = itemView.findViewById(R.id.content);
        seeFullNews = itemView.findViewById(R.id.seeFullNews);

        seeFullNews.setOnClickListener(onNewsClickListener);
//        category = (TextView) itemView.findViewById(R.id.category);
    }

    public void bind(News news) {
        this.news = news;

//        category.setText(news.getCategory().getName());
        title.setText(news.getTitle());
        description.setText(news.getSummary());

        final String newsTags = news.getTags();
        if (newsTags != null) {
            tags.setText(newsTags);
        } else {
            tags.setVisibility(View.GONE);
        }

        if (news.getImages() != null && !news.getImages().isEmpty()) {
            ImageLoader.loadGenericPictureToImageViewFit(cover, news.getImages().get(0));
        } else {
            cover.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener onNewsClickListener = view -> {
        if (onNewsViewListener != null) {
            onNewsViewListener.onNewsViewClick(news);
        }
    };

}
