package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.StringRes;

import in.ureport.R;
import in.ureport.managers.CognitoCredentialsLoginManager;
import in.ureport.managers.DynamoDBManager;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class CacheLoggedUserTask extends ProgressTask<User, Void, Boolean> {

    public CacheLoggedUserTask(Context context) {
        super(context, R.string.caching_user_load_message);
    }

    @Override
    protected Boolean doInBackground(User... users) {
        try {
            User user = users[0];
            user.save();

            SystemPreferences systemPreferences = new SystemPreferences(context);
            systemPreferences.setUserLoggedId(user.getIdentityId());
            systemPreferences.setCountryCode(user.getCountry());
            return true;
        } catch(Exception exception) {
            setException(exception);
        }
        return false;
    }

}
