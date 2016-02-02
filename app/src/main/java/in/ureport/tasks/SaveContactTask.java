package in.ureport.tasks;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import in.ureport.R;
import in.ureport.helpers.AnalyticsHelper;
import in.ureport.helpers.ContactBuilder;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.flowrunner.models.Contact;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.rapidpro.Field;
import in.ureport.network.ProxyApi;
import in.ureport.network.ProxyServices;
import in.ureport.network.RapidProServices;
import in.ureport.tasks.common.ProgressTask;
import retrofit.RetrofitError;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class SaveContactTask extends ProgressTask<User, Void, Contact> {

    private static final String TAG = "SaveContactTask";

    private RapidProServices rapidProServices;

    private CountryInfo countryInfo;

    public SaveContactTask(Context context, CountryInfo countryInfo) {
        super(context, R.string.load_message_save_user);
        this.countryInfo = countryInfo;
        this.rapidProServices = new RapidProServices();
    }

    public SaveContactTask(Context context) {
        super(context);
        this.rapidProServices = new RapidProServices();
    }

    @Override
    protected Contact doInBackground(User... params) {
        try {
            User user = params[0];
            String countryCode = countryInfo != null ? countryInfo.getIsoAlpha3() : user.getCountryProgram();

            String countryToken = getTokenFromProxy(countryCode);
            UserManager.updateCountryToken(countryToken);
            if (countryToken != null && !countryToken.isEmpty()) {
                Contact contact = getContactForUser(countryToken, user, countryCode);
                try {
                    return rapidProServices.saveContact(countryToken, contact);
                } catch (RetrofitError exception) {
                    AnalyticsHelper.sendException(exception);
                    Log.e(TAG, "doInBackground ", exception);
                }
                return rapidProServices.saveContact(countryToken, contact);
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
    private String getTokenFromProxy(String countryCode) {
        try {
            CountryProgram countryProgram = CountryProgramManager.getCountryProgramForCode(countryCode);

            ProxyServices proxyServices = new ProxyServices(getContext());
            ProxyApi.Response response = proxyServices.getAuthenticationTokenByCountry(countryProgram.getCode());
            return response.token;
        } catch(Exception exception) {
            return null;
        }
    }

    private Contact getContactForUser(String token, User user, String countryCode) {
        List<Field> fields = rapidProServices.loadFields(token);

        ContactBuilder contactBuilder = new ContactBuilder(fields);
        return contactBuilder.buildContactWithFields(user, countryCode);
    }

}
