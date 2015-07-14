package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class GetUserLoggedTask extends AsyncTask<Void, Void, User> {

    private Context context;

    public GetUserLoggedTask(Context context) {
        this.context = context;
    }

    @Override
    protected User doInBackground(Void... voids) {
        SystemPreferences preferences = new SystemPreferences(context);

        UserRepository userRepository = new UserBusiness();
        return userRepository.get(preferences.getUserLoggedId());
    }

}
