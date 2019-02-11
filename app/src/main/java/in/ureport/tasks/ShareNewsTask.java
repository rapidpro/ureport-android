package in.ureport.tasks;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import in.ureport.R;
import in.ureport.models.News;
import in.ureport.views.holders.NewsItemViewHolder;

/**
 * Created by johncordeiro on 02/10/15.
 */
public class ShareNewsTask extends ShareViewTask<News> {

    public ShareNewsTask(Fragment fragment, News object) {
        super(fragment, object, R.string.title_share_news);
    }

    @Override
    protected View createViewForObject(News object) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        NewsItemViewHolder itemViewHolder = new NewsItemViewHolder(inflater.inflate(R.layout.item_news, null), null);
        itemViewHolder.bind(object);
        return itemViewHolder.itemView;
    }
}
