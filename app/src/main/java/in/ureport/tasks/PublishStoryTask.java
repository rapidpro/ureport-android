package in.ureport.tasks;

import android.os.AsyncTask;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PublishStoryTask extends AsyncTask<Story, Void, Void> {

    private static final int USER_POINTS = 5;

    @Override
    protected Void doInBackground(Story... stories) {
        if(stories.length == 0) return null;

        UserRepository userRepository = new UserBusiness();
        User user = userRepository.get();

        addNewPoints(user);

        Story story = stories[0];
        story.setUser(user);
        story.save();

        return null;
    }

    private void addNewPoints(User user) {
        Integer points = user.getPoints();
        user.setPoints(points != null ? points + USER_POINTS : USER_POINTS);

        Integer storiesCount = user.getStories();
        user.setStories(storiesCount != null ? storiesCount + 1 : 1);

        user.save();
    }
}
