package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.User;
import in.ureport.models.holders.Login;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 7/7/15.
 */
public class LoginTask extends AsyncTask<Login, Void, User> {

    private static final String TAG = "LoginTask";

    private Context context;

    public LoginTask(Context context) {
        this.context = context;
    }

    @Override
    protected User doInBackground(Login... logins) {
        if(logins.length == 0) return null;

        try {
            UserRepository repository = new UserBusiness();
            User user = repository.login(logins[0]);
            saveUserLoggedId(user);

            return user;
        } catch (Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }

        return null;
    }

    private void saveUserLoggedId(User user) {
        if(user != null) {
            SystemPreferences systemPreferences = new SystemPreferences(context);
            systemPreferences.setUserLoggedId(user.getId());
            systemPreferences.setCountryCode(user.getCountry());
        }
    }
}
