package in.ureport.network;

import android.content.Context;

import in.ureport.R;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by John Cordeiro on 5/25/17.
 * Copyright © 2017 ureport-android, Inc. All rights reserved.
 */

public class FcmTopicServices {

    private static final String BASE_URL = "https://iid.googleapis.com/iid/";

    private FcmTopicApi api;
    private String fcmKey;

    public FcmTopicServices(Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .build();
        this.api = retrofit.create(FcmTopicApi.class);
        this.fcmKey = String.format(FcmServices.FCM_AUTHORIZATION, context.getString(R.string.fcm_api_key));
    }

    public Call<Void> registerOnTopic(String pushIdentity, String topic) {
        return api.registerOnTopic(fcmKey, pushIdentity, topic);
    }

    public Call<Void> unregisterFromTopic(String pushIdentity, String topic) {
        return api.unregisterFromTopic(fcmKey, pushIdentity, topic);
    }

}
