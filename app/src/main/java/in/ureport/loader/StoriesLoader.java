package in.ureport.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.ureport.db.business.StoryBusiness;
import in.ureport.db.repository.StoryRepository;
import in.ureport.managers.DynamoDBManager;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class StoriesLoader extends AsyncTaskLoader<List<Story>> {

    private static final String TAG = "StoriesLoader";

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
        try {
            DynamoDBScanExpression expression = new DynamoDBScanExpression();
            List<Story> stories = DynamoDBManager.getMapper().scan(Story.class, expression);
            loadUsersForStories(stories);

            return stories;
        } catch(Exception exception) {
            Log.e(TAG, "loadInBackground ", exception);
        }
        return new ArrayList<>();
    }

    private void loadUsersForStories(List<Story> stories) {
        List<Object> users = (List)getUsersFromStories(stories);
        List<User> usersLoaded = loadUsers(users);
        replaceStoriesWithUsersLoaded(stories, usersLoaded);
    }

    private void replaceStoriesWithUsersLoaded(List<Story> stories, List<User> usersLoaded) {
        for (Story story : stories) {
            User user = story.getUser();
            int userIndex = usersLoaded.indexOf(user);
            if(userIndex >= 0) story.setUser(usersLoaded.get(userIndex));
        }
    }

    private List<User> loadUsers(List<Object> users) {
        Map<String, List<Object>> itemsLoaded = DynamoDBManager.getMapper().batchLoad(users);
        return (List)itemsLoaded.get("User");
    }

    @NonNull
    private List<User> getUsersFromStories(List<Story> stories) {
        List<User> users = new ArrayList<>();
        for (Story story : stories) {
            if(!users.contains(story.getUser()))
                users.add(story.getUser());
        }
        return users;
    }

}
