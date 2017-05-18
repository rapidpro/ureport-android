package in.ureport.services.fcm;

import android.util.Log;

import io.rapidpro.sdk.core.models.v2.Contact;
import io.rapidpro.sdk.services.FcmClientRegistrationIntentService;

/**
 * Created by John Cordeiro on 5/15/17.
 * Copyright © 2017 ureport-android, Inc. All rights reserved.
 */

public class UreportFcmRegistrationService extends FcmClientRegistrationIntentService {

    private static final String TAG = "UreportFcmRegistrationS";

    @Override
    public void onFcmRegistered(String pushIdentity, Contact contact) {
        Log.d(TAG, "onFcmRegistered() called with: pushIdentity = [" + pushIdentity + "], contact = [" + contact + "]");
    }
}
