package in.ureport.tasks;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import in.ureport.R;
import in.ureport.helpers.ContactBuilder;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.models.rapidpro.Contact;
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
    private Locale locale;

    private RapidProServices rapidProServices;

    public SaveContactTask(Context context, Locale locale) {
        super(context, R.string.load_message_save_user);
        this.locale = locale;
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
            getLocaleByUserIfNeeded(user);

            String countryToken = getTokenFromProxy();
            UserManager.updateCountryToken(countryToken);
            if (countryToken != null && !countryToken.isEmpty()) {
                Contact contact = getContactForUser(user, countryToken);
                try {
                    return rapidProServices.saveContact(countryToken, contact);
                } catch (RetrofitError exception) {
                    Log.e(TAG, "doInBackground ", exception);
                }
                return rapidProServices.saveContact(countryToken, contact);
            } else {
                ContactBuilder contactBuilder = new ContactBuilder();
                return contactBuilder.buildContactWithoutFields(user);
            }
        } catch (Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

    private void getLocaleByUserIfNeeded(User user) {
        if(locale == null) {
            locale = getLocaleByUser(user);
        }
    }

    @Nullable
    private String getTokenFromProxy() {
        try {
            CountryProgram countryProgram = CountryProgramManager.getCountryProgramForCode(locale.getISO3Country());

            ProxyServices proxyServices = new ProxyServices(getContext());
            ProxyApi.Response response = proxyServices.getAuthenticationTokenByCountry(countryProgram.getCode());
            return response.token;
        } catch(Exception exception) {
            return null;
        }
    }

    private Contact getContactForUser(User user, String token) {
        List<Field> fields = rapidProServices.loadFields(token);

        ContactBuilder contactBuilder = new ContactBuilder(fields);
        return contactBuilder.buildContactWithFields(user, locale);
    }

    @Nullable
    private Locale getLocaleByUser(User user) {
        Locale [] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            try {
                if (hasUserISOCode(locale, user)) {
                    return locale;
                }
            } catch(Exception ignored){}
        }
        return null;
    }

    private boolean hasUserISOCode(Locale locale, User user) {
        return locale.getDisplayCountry() != null && locale.getISO3Country() != null
                && locale.getISO3Country().equals(user.getCountry());
    }

}
