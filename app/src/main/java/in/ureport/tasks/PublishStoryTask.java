package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PublishStoryTask extends AsyncTask<Story, Void, Void> {

    private Context context;

    public PublishStoryTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Story... stories) {
        if(stories.length == 0) return null;

        SystemPreferences preferences = new SystemPreferences(context);

        UserRepository userRepository = new UserBusiness();
        User user = userRepository.get(preferences.getUserLoggedId());

        Story story = stories[0];
        story.setUser(user);
        story.save();

        return null;
    }
}
