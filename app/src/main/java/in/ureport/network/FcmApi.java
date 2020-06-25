package in.ureport.network;

import in.ureport.models.gcm.GcmInput;
import in.ureport.models.gcm.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by johncordeiro on 21/08/15.
 */
public interface FcmApi {

    @POST("/fcm/send")
    Response sendData(@Header("Authorization") String senderId, @Body GcmInput gcmInput);

}
