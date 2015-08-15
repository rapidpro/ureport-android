package in.ureport.network;

import android.support.annotation.NonNull;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;

import java.util.ArrayList;
import java.util.List;

import in.ureport.managers.DynamoDBManager;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class ContributionServices {

    public List<Contribution> loadContributionForStory(Story story) {
        Contribution contribution = new Contribution();
        contribution.setStory(story);

        DynamoDBQueryExpression<Contribution> queryExpression = new DynamoDBQueryExpression<Contribution>()
                .withIndexName("createdDate-index")
                .withHashKeyValues(contribution)
                .withConsistentRead(false);

        return new ArrayList<>(DynamoDBManager.getMapper().query(Contribution.class, queryExpression));
    }

    public void loadUsersForContributions(List<Contribution> contributions) {
        List<Object> users = (List)getUsersFromContributions(contributions);

        UserServices services = new UserServices();
        List<User> usersLoaded = services.loadUsers(users);
        replaceStoriesWithUsersLoaded(contributions, usersLoaded);
    }

    private void replaceStoriesWithUsersLoaded(List<Contribution> contributions, List<User> usersLoaded) {
        for (Contribution contribution : contributions) {
            User user = contribution.getAuthor();
            int userIndex = usersLoaded.indexOf(user);
            if(userIndex >= 0) contribution.setAuthor(usersLoaded.get(userIndex));
        }
    }

    @NonNull
    private List<User> getUsersFromContributions(List<Contribution> contributions) {
        List<User> users = new ArrayList<>();
        for (Contribution contribution : contributions) {
            if(!users.contains(contribution.getAuthor()))
                users.add(contribution.getAuthor());
        }
        return users;
    }
    
}
