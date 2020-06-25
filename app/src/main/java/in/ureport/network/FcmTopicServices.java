package in.ureport.network;

import android.content.Context;

import in.ureport.R;
import retrofit2.Call;

/**
 * Created by John Cordeiro on 5/25/17.
 * Copyright © 2017 ureport-android, Inc. All rights reserved.
 */

public class FcmTopicServices {

    private static final String BASE_URL = "https://iid.googleapis.com/iid/";

    private FcmTopicApi api;
    private String fcmKey;

    public FcmTopicServices(Context context) {
        this.api = ServiceFactory.create(FcmTopicApi.class, BASE_URL);
        this.fcmKey = String.format(FcmServices.FCM_AUTHORIZATION, context.getString(R.string.fcm_api_key));
    }

    public Call<Void> registerOnTopic(String pushIdentity, String topic) {
        return api.registerOnTopic(fcmKey, pushIdentity, topic);
    }

    public Call<Void> unregisterFromTopic(String pushIdentity, String topic) {
        return api.unregisterFromTopic(fcmKey, pushIdentity, topic);
    }

}
