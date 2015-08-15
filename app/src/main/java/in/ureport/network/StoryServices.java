package in.ureport.network;

import android.support.annotation.NonNull;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;

import java.util.ArrayList;
import java.util.List;

import in.ureport.managers.DynamoDBManager;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class StoryServices {

    public List<Story> loadAll() {
        DynamoDBScanExpression expression = new DynamoDBScanExpression();
        return new ArrayList<>(DynamoDBManager.getMapper().scan(Story.class, expression));
    }

    public void loadUsersForStories(List<Story> stories) {
        List<Object> users = (List)getUsersFromStories(stories);

        UserServices services = new UserServices();
        List<User> usersLoaded = services.loadUsers(users);
        replaceStoriesWithUsersLoaded(stories, usersLoaded);
    }

    private void replaceStoriesWithUsersLoaded(List<Story> stories, List<User> usersLoaded) {
        for (Story story : stories) {
            User user = story.getUser();
            int userIndex = usersLoaded.indexOf(user);
            if(userIndex >= 0) story.setUser(usersLoaded.get(userIndex));
        }
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
