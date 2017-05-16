package in.ureport.services.fcm;

import io.rapidpro.sdk.core.models.Contact;
import io.rapidpro.sdk.services.FcmClientRegistrationIntentService;

/**
 * Created by John Cordeiro on 5/15/17.
 * Copyright © 2017 ureport-android, Inc. All rights reserved.
 */

public class UreportFcmRegistrationService extends FcmClientRegistrationIntentService {

    @Override
    public void onFcmRegistered(String pushIdentity, Contact contact) {
        super.onFcmRegistered(pushIdentity, contact);


    }
}
