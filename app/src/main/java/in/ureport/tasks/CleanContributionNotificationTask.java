package in.ureport.tasks;

import android.content.Context;
import android.util.Log;

import in.ureport.db.business.ContributionNotificationBusiness;
import in.ureport.db.repository.ContributionNotificationRepository;
import in.ureport.models.Story;

/**
 * Created by johncordeiro on 07/10/15.
 */
public class CleanContributionNotificationTask extends NotificationTask<Story, Void, Void> {

    private static final String TAG = "CleanContribution";

    private static final String CLEAN_CONTRIBUTION_TYPE = "cleanContributionNotification";

    public CleanContributionNotificationTask(Context context) {
        super(context);
    }

    @Override
    protected Void doInBackground(Story... params) {
        try {
            Story story = params[0];

            ContributionNotificationRepository repository = new ContributionNotificationBusiness();
            repository.deleteByStoryId(story.getKey());
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

    @Override
    protected String getNotificationType() {
        return CLEAN_CONTRIBUTION_TYPE;
    }

}
