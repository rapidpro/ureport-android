package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import in.ureport.db.business.StoryBusiness;
import in.ureport.db.repository.StoryRepository;
import in.ureport.models.Story;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoriesLoader extends AsyncTaskLoader<List<Story>> {

    public StoriesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Story> loadInBackground() {
        StoryRepository repository = new StoryBusiness();
        return repository.getAll();
    }

}
