package in.ureport.services.fcm;

import android.support.v4.app.NotificationCompat;

import in.ureport.R;
import io.rapidpro.sdk.services.FcmClientIntentService;

/**
 * Created by John Cordeiro on 5/19/17.
 * Copyright © 2017 ureport-android, Inc. All rights reserved.
 */

public class UreportFcmReceivedListener extends FcmClientIntentService {

    @Override
    protected void onCreateLocalNotification(NotificationCompat.Builder builder) {
        builder.setSmallIcon(R.drawable.icon_notification);
        super.onCreateLocalNotification(builder);
    }
}
