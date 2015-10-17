package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;

import in.ureport.managers.LocalNotificationManager;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.models.db.ContributionNotification;

/**
 * Created by johncordeiro on 07/10/15.
 */
public class ContributionNotificationTask extends NotificationTask<Contribution, Void, Void> {

    private static final String TAG = "ContributionNotif";
    public static final String NEW_CONTRIBUTION_TYPE = "newContributionNotification";

    private Story story;

    public ContributionNotificationTask(Context context, Story story) {
        super(context);
        this.story = story;
    }

    @Override
    protected Void doInBackground(Contribution... params) {
        try {
            Contribution contribution = params[0];

            ContributionNotification contributionNotification = buildContributionNotification(contribution);
            contributionNotification.save();

            LocalNotificationManager localNotificationManager = new LocalNotificationManager(context);
            localNotificationManager.sendContributionNotification(contribution, story);
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

    @NonNull
    private ContributionNotification buildContributionNotification(Contribution contribution) {
        ContributionNotification contributionNotification = new ContributionNotification();
        contributionNotification.setDate(new Date());
        contributionNotification.setMessage(contribution.getContent());
        contributionNotification.setStoryId(story.getKey());

        User author = contribution.getAuthor();
        if(author != null) {
            contributionNotification.setPicture(author.getPicture());
            contributionNotification.setNickname(author.getNickname());
        }
        return contributionNotification;
    }

    @Override
    protected String getNotificationType() {
        return NEW_CONTRIBUTION_TYPE;
    }
}
