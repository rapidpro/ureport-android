package in.ureport.tasks;

import android.content.Context;

import in.ureport.R;
import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.managers.DynamoDBManager;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 7/16/15.
 */
public class PublishStoryTask extends ProgressTask<Story, Void, Void> {

    private static final int USER_POINTS = 5;

    public PublishStoryTask(Context context) {
        super(context, R.string.publishing_story_load_message);
    }

    @Override
    protected Void doInBackground(Story... stories) {
        if(stories.length == 0) return null;

        try {
            UserRepository userRepository = new UserBusiness();
            User user = userRepository.get();

            addNewPoints(user);

            Story story = stories[0];
            story.setUser(user);
            DynamoDBManager.getMapper().save(story);
        } catch(Exception exception) {
            setException(exception);
        }
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
