package in.ureport.network;

import in.ureport.models.rapidpro.Boundary;
import in.ureport.models.rapidpro.Contact;
import in.ureport.models.rapidpro.Field;
import in.ureport.models.rapidpro.Group;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by johncordeiro on 18/08/15.
 */
public interface RapidProApi {

    @GET("/groups.json")
    Response<Group> listGroups(@Header("Authorization") String apiKey);

    @GET("/fields.json")
    Response<Field> listFields(@Header("Authorization") String apiKey);

    @GET("/boundaries.json?aliases=true")
    Response<Boundary> listBoundaries(@Header("Authorization") String apiKey, @Query("page") Integer page);

    @POST("/contacts.json")
    Contact saveContact(@Header("Authorization") String apiKey, @Body Contact contact);

}
