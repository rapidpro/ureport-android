package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.ureport.db.business.StoryBusiness;
import in.ureport.db.repository.StoryRepository;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoriesLoader extends AsyncTaskLoader<List<Story>> {

    private User user;
    private boolean publicType = true;

    public StoriesLoader(Context context) {
        super(context);
    }

    public StoriesLoader(Context context, User user) {
        super(context);
        this.user = user;
        publicType = false;
    }

    @Override
    public List<Story> loadInBackground() {
        StoryRepository repository = new StoryBusiness();
        if(publicType) {
            return repository.getAll();
        } else {
            return user != null ? repository.getStoryByUser(user) : new ArrayList<Story>();
        }
    }

}
