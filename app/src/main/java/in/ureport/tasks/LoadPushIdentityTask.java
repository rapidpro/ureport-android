package in.ureport.tasks;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import in.ureport.R;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class LoadPushIdentityTask extends ProgressTask<Void, Void, String> {

    private static final String TAG = "LoadPushIdentity";

    public LoadPushIdentityTask(Context context) {
        super(context, R.string.load_message_user_data);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            InstanceID instanceID = InstanceID.getInstance(context);
            return instanceID.getToken(context.getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch(Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }
}
