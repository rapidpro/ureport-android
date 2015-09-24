package in.ureport.network;

import android.content.Context;

import com.firebase.client.ChildEventListener;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;

import in.ureport.BuildConfig;
import in.ureport.helpers.GsonDateDeserializer;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.UserManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.rapidpro.Boundary;
import in.ureport.models.rapidpro.Contact;
import in.ureport.models.rapidpro.Group;
import in.ureport.models.rapidpro.Response;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class RapidProServices {

    private static final String TAG = "RapidProServices";

    private static final String ENDPOINT = "https://api.rapidpro.io/api/v1";

    private final RapidProApi service;

    private static final String path = "rapidpro";
    private static final String responsePath = "response";
    private static final String messagePath = "message";

    public RapidProServices() {
        RestAdapter restAdapter = buildRestAdapter();
        if(BuildConfig.DEBUG) restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        service = restAdapter.create(RapidProApi.class);
    }

    public RapidProApi.Response<Boundary> loadBoundaries(String apiKey) {
        return service.listBoundaries(apiKey);
    }

    public List<Group> loadGroups(String apiKey) {
        RapidProApi.Response<Group> response = service.listGroups(apiKey);
        return response.getResults();
    }

    public Contact saveContact(String apiKey, Contact contact) {
        return service.saveContact(apiKey, contact);
    }

    private RestAdapter buildRestAdapter() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new GsonDateDeserializer())
                .create();

        return new RestAdapter.Builder()
                    .setEndpoint(ENDPOINT)
                    .setConverter(new GsonConverter(gson))
                    .build();
    }

    public void removeLastMessageChildEventListener(ChildEventListener listener) {
        if(UserManager.getUserId() != null) {
            FirebaseManager.getReference().child(path).child(messagePath).child(getRapidproUserId())
                    .removeEventListener(listener);
        }
    }

    private String getRapidproUserId() {
        return UserManager.getUserId().replace(":", "").replace("-", "");
    }

    public void addLastMessageChildEventListener(ChildEventListener listener) {
        if(UserManager.getUserId() != null) {
            FirebaseManager.getReference().child(path).child(messagePath).child(getRapidproUserId()).limitToLast(1)
                    .addChildEventListener(listener);
        }
    }

    public void sendMessage(Context context, String message) {
        CountryProgram countryProgram = CountryProgramManager.getCurrentCountryProgram();
        String channel = context.getString(countryProgram.getChannel());

        Response response = new Response(channel, UserManager.getUserId(), message);
        FirebaseManager.getReference().child(path).child(responsePath).push().setValue(response);
    }

}
