package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class UsersLoader extends AsyncTaskLoader<List<User>> {

    public UsersLoader(Context context) {
        super(context);
    }

    @Override
    public List<User> loadInBackground() {
        SystemPreferences systemPreferences = new SystemPreferences(getContext());
        Long userLoggedId = systemPreferences.getUserLoggedId();

        UserRepository repository = new UserBusiness();

        if(!userLoggedId.equals(SystemPreferences.USER_NO_LOGGED_ID)) {
            User user = repository.get(userLoggedId);
            return repository.getAllExcluding(user);
        } else {
            return repository.getAll();
        }
    }

}
