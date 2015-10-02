package in.ureport.tasks;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import in.ureport.R;
import in.ureport.helpers.ContactBuilder;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.models.rapidpro.Contact;
import in.ureport.models.rapidpro.Field;
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

    @Override
    protected Contact doInBackground(User... params) {
        try {
            User user = params[0];

            CountryProgram countryProgram = CountryProgramManager.getCountryProgramForCode(user.getCountry());
            String token = context.getString(countryProgram.getApiToken());

            Contact contact = getContactForUser(user, token);

            if (CountryProgramManager.isCountryProgramEnabled(countryProgram)) {
                try {
                    return rapidProServices.saveContact(token, contact);
                } catch (RetrofitError exception) {
                    Log.e(TAG, "doInBackground ", exception);
                }
                return rapidProServices.saveContact(token, contact);
            } else {
                return contact;
            }
        } catch (Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
        return null;
    }

    private Contact getContactForUser(User user, String token) {
        List<Field> fields = rapidProServices.loadFields(token);

        ContactBuilder contactBuilder = new ContactBuilder(fields);
        return contactBuilder.buildContactByUser(user, locale);
    }

}
