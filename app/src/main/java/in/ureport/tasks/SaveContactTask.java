package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import in.ureport.BuildConfig;
import in.ureport.R;
import in.ureport.helpers.AnalyticsHelper;
import in.ureport.helpers.ContactBuilder;
import in.ureport.helpers.IOHelper;
import in.ureport.helpers.SntpClient;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.ip.ProxyResponse;
import in.ureport.network.ProxyServices;
import io.rapidpro.sdk.FcmClient;
import io.rapidpro.sdk.core.models.Field;
import io.rapidpro.sdk.core.models.base.ContactBase;
import io.rapidpro.sdk.core.models.v1.Contact;
import io.rapidpro.sdk.core.network.RapidProServices;
import retrofit.RetrofitError;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class SaveContactTask extends AsyncTask<Void, Void, ContactBase> {

    private static final String TAG = "SaveContactTask";

    private RapidProServices rapidProServices;

    private CountryInfo countryInfo;
    private Contact currentContact;
    private User user;
    private final boolean newUser;

    private CountryProgram countryProgram;
    private String rapidproEndpoint;
    private String countryInfoJson;

    private String proxyToken;

    protected SaveContactTask(Context context, User user, boolean newUser) {
        this.user = user;
        this.newUser = newUser;
        this.countryProgram = getCountryProgram(user);
        this.rapidproEndpoint = context.getString(countryProgram.getRapidproEndpoint());
        this.countryInfoJson = IOHelper.loadJSONFromAsset(context, "countryInfo.json");

        if (BuildConfig.SANDBOX_ORG) {
            proxyToken = context.getString(R.string.fcm_client_token);
        } else {
            ProxyServices proxyServices = new ProxyServices(context);
            try {
                ProxyResponse response = proxyServices.getAuthenticationTokenByCountry(countryProgram.getCode());
                proxyToken = response.getToken();
            } catch(Exception exception) { proxyToken = null; }
        }
    }

    protected SaveContactTask(Context context, User user, boolean newUser, CountryInfo countryInfo) {
        this(context, user, newUser);
        this.countryInfo = countryInfo;
    }

    @Override
    protected ContactBase doInBackground(Void... params) {
        ContactBase contact = null;
        try {
            rapidProServices = new RapidProServices(rapidproEndpoint, proxyToken);

            UserManager.updateCountryToken(proxyToken);
            if (proxyToken != null && !proxyToken.isEmpty()) {
                currentContact = loadCurrentContact(user);
                UserManager.initializeFcmClient(countryProgram);

                contact = saveContact(buildContact(user, getRegistrationDate(), countryProgram));
                FcmClient.registerContact(user.getKey(), contact.getUuid());
                return contact;
            }
            ContactBuilder contactBuilder = new ContactBuilder();
            contact = contactBuilder.buildContactWithoutFields(user);
        } catch (Exception exception) {
            AnalyticsHelper.sendException(exception);
            Log.e(TAG, "doInBackground ", exception);
        }
        return contact;
    }

    private CountryProgram getCountryProgram(User user) {
        String countryProgramCode = countryInfo != null ? countryInfo.getIsoAlpha3() : user.getCountryProgram();
        return CountryProgramManager.getCountryProgramForCode(countryProgramCode);
    }

    private io.rapidpro.sdk.core.models.v1.Contact loadCurrentContact(User user) {
        String extUrn = ContactBuilder.formatExtUrn(user.getKey());
        String fcmUrn = ContactBuilder.formatFcmUrn(user.getKey());

        io.rapidpro.sdk.core.models.v1.Contact contact = loadContactWithUrn(extUrn);
        if (contact == null) {
            contact = loadContactWithUrn(fcmUrn);
        }
        return contact;
    }

    private ContactBase saveContact(Contact contact) throws IOException {
        contact.setUrns(null);
        updateContactWithUuid(contact);
        updateContactWithGroups(contact);
        try {
            return rapidProServices.saveContactV1(contact).execute().body();
        } catch (RetrofitError exception) {
            AnalyticsHelper.sendException(exception);
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

    private void updateContactWithUuid(Contact contact) {
        if (currentContact != null) {
            contact.setUuid(currentContact.getUuid());
        }
    }

    private void updateContactWithGroups(Contact contact) {
        try {
            if (contactHasGroups(currentContact))
                contact.setGroups(null);
        } catch(Exception exception) {
            Log.e(TAG, "updateContactWithGroups: ", exception);
        }
    }

    private io.rapidpro.sdk.core.models.v1.Contact loadContactWithUrn(String urn) {
        try {
            return rapidProServices.loadContactV1(urn).execute().body().getResults().get(0);
        } catch (Exception exception) {
            return null;
        }
    }

    private boolean contactHasGroups(io.rapidpro.sdk.core.models.v1.Contact contactResult) {
        return contactResult != null && contactResult.getGroups() != null && !contactResult.getGroups().isEmpty();
    }

    private Date getRegistrationDate() {
        if(!newUser) return null;

        Date now = new Date();
        try {
            SntpClient client = new SntpClient();
            if (client.requestTime("pool.ntp.org", 4000)) {
                long ntpTimeMillis = client.getNtpTime();
                if (ntpTimeMillis > 0)
                    now = new Date(ntpTimeMillis);
            }
        } catch(Exception exception) {
            Log.e(TAG, "getRegistrationDate: ", exception);
        }
        return now;
    }

    private String getISO2CountryCode(String countryCode) {
        List<CountryInfo> countryInfo = getCountryInfoList();
        if(countryInfo != null) {
            for (CountryInfo info : countryInfo) {
                if (info.getIsoAlpha3().equalsIgnoreCase(countryCode)) {
                    countryCode = info.getCountryCode();
                    break;
                }
            }
        }
        return countryCode;
    }

    private Contact buildContact(User user, Date registrationDate, CountryProgram countryProgram) throws IOException {
        String userCountry = countryInfo != null ? countryInfo.getCountryCode() : user.getCountry();
        String countryCode = getISO2CountryCode(userCountry);

        List<Field> fields = rapidProServices.loadFields().execute().body().getResults();

        ContactBuilder contactBuilder = new ContactBuilder(fields);
        return contactBuilder.buildContactWithFields(user, registrationDate, countryCode, countryProgram);
    }

    private List<CountryInfo> getCountryInfoList() {
        try {
            Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
            Type type = new TypeToken<List<CountryInfo>>(){}.getType();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(countryInfoJson);

            return gson.fromJson(jsonObject.get("geonames"), type);
        } catch (Exception exception) {
            Log.e(TAG, "doInBackground: ", exception);
        }
        return null;
    }

    public interface Listener {
        void onStart();
        void onFinished(ContactBase contact, User user);
    }

}
