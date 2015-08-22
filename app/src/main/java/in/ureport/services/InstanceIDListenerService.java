package in.ureport.services;

import android.content.Intent;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);
    }
}
