package in.ureport.network;

import java.util.Map;

import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRun;
import in.ureport.flowrunner.models.FlowStepSet;
import in.ureport.models.rapidpro.Boundary;
import in.ureport.flowrunner.models.Contact;
import in.ureport.models.rapidpro.Field;
import in.ureport.models.rapidpro.Group;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by johncordeiro on 18/08/15.
 */
public interface RapidProApi {

    @FormUrlEncoded
    @POST("/external/received/{channel}/")
    retrofit.client.Response sendReceivedMessage(@Header("Authorization") String apiKey
            , @Path("channel") String channel
            , @retrofit.http.Field("from") String from
            , @retrofit.http.Field("text") String text);

    @GET("/groups.json")
    Response<Group> listGroups(@Header("Authorization") String apiKey, @Query("uuid") String contact);

    @GET("/fields.json")
    Response<Field> listFields(@Header("Authorization") String apiKey);

    @GET("/boundaries.json")
    Response<Boundary> listBoundaries(@Header("Authorization") String apiKey
            , @Query("page") Integer page, @Query("aliases") Boolean aliases);

    @GET("/runs.json")
    Response<FlowRun> listRuns(@Header("Authorization") String apiKey
            , @Query("contact") String uuid, @Query("after") String after);

    @GET("/flow_definition.json")
    FlowDefinition loadFlowDefinition(@Header("Authorization") String apiKey, @Query("uuid") String flowUuid);

    @POST("/steps")
    @Headers({ "Accept: application/json", "Content-Type: application/json" })
    Map<String, Object> saveFlowStepSet(@Header("Authorization") String apiKey, @Body FlowStepSet flowStepSet);

    @GET("/contacts.json")
    Response<Contact> loadContact(@Header("Authorization") String apiKey, @Query("urns") String urn);

    @POST("/contacts.json")
    Contact saveContact(@Header("Authorization") String apiKey, @Body Contact contact);

}
