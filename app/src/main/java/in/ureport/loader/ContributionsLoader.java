package in.ureport.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;

import java.util.ArrayList;
import java.util.List;

import in.ureport.managers.DynamoDBManager;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.network.ContributionServices;
import in.ureport.network.UserServices;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class ContributionsLoader extends AsyncTaskLoader<List<Contribution>> {

    private Story story;

    public ContributionsLoader(Context context, Story story) {
        super(context);
        this.story = story;
    }

    @Override
    public List<Contribution> loadInBackground() {
        ContributionServices contributionServices = new ContributionServices();

        List<Contribution> contributions = contributionServices.loadContributionForStory(story);
        contributionServices.loadUsersForContributions(contributions);

        return contributions;
    }

}
