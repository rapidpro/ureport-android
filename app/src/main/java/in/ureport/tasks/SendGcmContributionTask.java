package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.network.GcmServices;

/**
 * Created by johncordeiro on 06/10/15.
 */
public class SendGcmContributionTask extends AsyncTask<Contribution, Void, Void> {

    private static final String TAG = "SendGcmContribution";

    private Context context;
    private Story story;

    public SendGcmContributionTask(Context context, Story story) {
        this.context = context;
        this.story = story;
    }

    @Override
    protected Void doInBackground(Contribution... params) {
        try {
            Contribution contribution = params[0];

            GcmServices gcmServices = new GcmServices(context);
            gcmServices.sendContribution(story, contribution);
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

}
