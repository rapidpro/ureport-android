package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

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
        return newsList;
    }

}
