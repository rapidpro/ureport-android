package in.ureport.tasks;

import android.content.Context;
import android.support.annotation.NonNull;

import in.ureport.R;
import in.ureport.managers.DynamoDBManager;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class PublishContributionTask extends ProgressTask<Contribution, Void, Void> {

    public PublishContributionTask(Context context) {
        super(context, R.string.load_message_publishing_contribution);
    }

    @Override
    protected Void doInBackground(Contribution... contributions) {
        try {
            Contribution contribution = contributions[0];

            Story story = incrementStoryContribution(contribution);
            DynamoDBManager.getMapper().save(story);

            DynamoDBManager.getMapper().save(contribution);
        } catch(Exception exception) {
            setException(exception);
        }
        return null;
    }

    @NonNull
    private Story incrementStoryContribution(Contribution contribution) {
        Story story = contribution.getStory();
        Integer contributionCount = story.getContributions() != null ? story.getContributions() : 0;
        story.setContributions(contributionCount+1);
        return story;
    }

}
