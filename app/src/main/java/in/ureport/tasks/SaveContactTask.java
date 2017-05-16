package in.ureport.tasks;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import in.ureport.BuildConfig;
import in.ureport.R;
import in.ureport.flowrunner.models.Contact;
import in.ureport.helpers.AnalyticsHelper;
import in.ureport.helpers.ContactBuilder;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.ip.ProxyResponse;
import in.ureport.network.ProxyServices;
import in.ureport.tasks.common.ProgressTask;
import io.rapidpro.sdk.FcmClient;
import io.rapidpro.sdk.core.network.RapidProServices;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class SaveContactTask extends ProgressTask<User, Void, Contact> {

    private static final String TAG = "SaveContactTask";

//    private RapidProServices rapidProServices;
    private RapidProServices rapidProServices;

    private CountryInfo countryInfo;
    private final boolean newUser;

    public SaveContactTask(Context context, CountryInfo countryInfo, boolean newUser) {
        super(context, R.string.load_message_save_user);
        this.countryInfo = countryInfo;
        this.newUser = newUser;
    }

    public SaveContactTask(Context context, boolean newUser) {
        super(context);
        this.newUser = newUser;
    }

    @Override
    protected Contact doInBackground(User... params) {
        try {
            User user = params[0];
            String countryProgramCode = countryInfo != null ? countryInfo.getIsoAlpha3() : user.getCountryProgram();
            CountryProgram countryProgram = CountryProgramManager.getCountryProgramForCode(countryProgramCode);

            String rapidproEndpoint = getContext().getString(countryProgram.getRapidproEndpoint());
            String countryToken = getTokenFromProxy(countryProgram);

            rapidProServices = new RapidProServices(rapidproEndpoint, countryToken);

            UserManager.updateCountryToken(countryToken);
            if (countryToken != null && !countryToken.isEmpty()) {
                UserManager.initializeFcmClient(countryProgram);
                FcmClient.registerContact(user.getKey());

                ContactBuilder contactBuilder = new ContactBuilder();
                return contactBuilder.buildContactWithoutFields(user);

//                String countryCode = countryInfo != null ? countryInfo.getCountryCode() : user.getCountry();
//                Contact contact = getContactForUser(countryToken, user, getRegistrationDate()
//                        , getISO2CountryCode(countryCode), countryProgram);
//                updateContactsWithGroups(countryToken, contact);

//                try {
//                    return rapidProServices.saveContact(countryToken, contact);
//                } catch (RetrofitError exception) {
//                    AnalyticsHelper.sendException(exception);
//                    Log.e(TAG, "doInBackground ", exception);
//                }
//                return rapidProServices.saveContact(countryToken, contact);
            } else {
                ContactBuilder contactBuilder = new ContactBuilder();
                return contactBuilder.buildContactWithoutFields(user);
            }
        } catch (Exception exception) {
            AnalyticsHelper.sendException(exception);
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

    @Nullable
    private String getTokenFromProxy(CountryProgram countryProgram) {
        if (BuildConfig.DEBUG) {
            return getContext().getString(R.string.fcm_client_token);
        }

        try {
            ProxyServices proxyServices = new ProxyServices(getContext());
            ProxyResponse response = proxyServices.getAuthenticationTokenByCountry(countryProgram.getCode());
            return response.getToken();
        } catch(Exception exception) {
            return null;
        }
    }

//    private void updateContactsWithGroups(String countryToken, Contact contact) {
//        try {
//            Contact contactResult = this.rapidProServices.loadContact(countryToken, contact.getUrns().get(0));
//            if (contactHasGroups(contactResult))
//                contact.setGroups(null);
//        } catch(Exception exception) {
//            Log.e(TAG, "updateContactsWithGroups: ", exception);
//        }
//    }
//
//    private boolean contactHasGroups(Contact contactResult) {
//        return contactResult != null && contactResult.getGroups() != null && !contactResult.getGroups().isEmpty();
//    }
//
//    private Date getRegistrationDate() {
//        if(!newUser) return null;
//
//        Date now = new Date();
//        try {
//            SntpClient client = new SntpClient();
//            if (client.requestTime("pool.ntp.org", 4000)) {
//                long ntpTimeMillis = client.getNtpTime();
//                Log.i(TAG, "getRegistrationDate: ntpTimeMillis: " + new Date(ntpTimeMillis));
//                if (ntpTimeMillis > 0)
//                    now = new Date(ntpTimeMillis);
//            }
//        } catch(Exception exception) {
//            Log.e(TAG, "getRegistrationDate: ", exception);
//        }
//        return now;
//    }
//
//    private String getISO2CountryCode(String countryCode) {
//        List<CountryInfo> countryInfo = getCountryInfoList();
//        if(countryInfo != null) {
//            for (CountryInfo info : countryInfo) {
//                if (info.getIsoAlpha3().equalsIgnoreCase(countryCode)) {
//                    countryCode = info.getCountryCode();
//                    break;
//                }
//            }
//        }
//        return countryCode;
//    }
//
//    private Contact getContactForUser(String token, User user, Date registrationDate, String countryCode, CountryProgram countryProgram) {
//        List<Field> fields = rapidProServices.loadFields(token);
//
//        ContactBuilder contactBuilder = new ContactBuilder(fields);
//        return contactBuilder.buildContactWithFields(user, registrationDate, countryCode, countryProgram);
//    }
//
//    private List<CountryInfo> getCountryInfoList() {
//        try {
//            String json = IOHelper.loadJSONFromAsset(getContext(), "countryInfo.json");
//
//            Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
//            Type type = new TypeToken<List<CountryInfo>>(){}.getType();
//
//            JsonParser jsonParser = new JsonParser();
//            JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
//
//            return gson.fromJson(jsonObject.get("geonames"), type);
//        } catch (Exception exception) {
//            Log.e(TAG, "doInBackground: ", exception);
//        }
//        return null;
//    }

}
