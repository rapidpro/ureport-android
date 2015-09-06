package in.ureport.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import in.ureport.R;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.GcmTopicManager;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.network.UserServices;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "GcmRegistrationIntent";

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            final String pushIdentity = instanceID.getToken(getString(R.string.gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            FirebaseManager.init(this);
            String userKey = UserManager.getUserId();

            if(userKey != null) {
                updateUserInfo(pushIdentity, userKey);
            }
        } catch(Exception exception) {
            Log.e(TAG, "onHandleIntent ", exception);
        }
    }

    private void updateUserInfo(final String pushIdentity, String userKey) {
        UserServices userServices = new UserServices();
        userServices.updatePushIdentity(userKey, pushIdentity);
        userServices.getUser(userKey, new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                final User user = dataSnapshot.getValue(User.class);
                registerTopics(user, pushIdentity);
            }
        });
    }

    private void registerTopics(final User user, final String pushIdentity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                GcmTopicManager gcmTopicManager = new GcmTopicManager(GcmRegistrationIntentService.this);
                gcmTopicManager.registerUserTopics(pushIdentity, user);
                return null;
            }
        }.execute();
    }
}
