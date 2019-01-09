package in.ureport.network;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by John Cordeiro on 5/25/17.
 * Copyright © 2017 ureport-android, Inc. All rights reserved.
 */

interface FcmTopicApi {

    @POST("v1/{pushIdentity}/rel/topics/{topic}")
    Call<Void> registerOnTopic(@Header("Authorization") String key,
                               @Path("pushIdentity") String pushIdentity,
                               @Path("topic") String topic);

    @DELETE("v1/{pushIdentity}/rel/topics/{topic}")
    Call<Void> unregisterFromTopic(@Header("Authorization") String key,
                                   @Path("pushIdentity") String pushIdentity,
                                   @Path("topic") String topic);

}
