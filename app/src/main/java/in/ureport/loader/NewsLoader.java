package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.models.News;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    public NewsLoader(Context context) {
        super(context);
    }

    @Override
    public List<News> loadInBackground() {
        List<News> newsList = new ArrayList<>();

        News news1 = new News(getContext().getString(R.string.news1_title)
                , getContext().getString(R.string.news1_author)
                , getContext().getString(R.string.news1_content)
                , R.drawable.news1_cover);

        News news2 = new News(getContext().getString(R.string.news2_title)
                , getContext().getString(R.string.news2_author)
                , getContext().getString(R.string.news2_content)
                , R.drawable.news2_cover);

        News news3 = new News(getContext().getString(R.string.news3_title)
                , getContext().getString(R.string.news3_author)
                , getContext().getString(R.string.news3_content)
                , R.drawable.news3_cover);

        newsList.add(news1);
        newsList.add(news2);
        newsList.add(news3);

        return newsList;
    }

}
