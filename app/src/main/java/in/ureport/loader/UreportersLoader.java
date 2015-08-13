package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class UreportersLoader extends AsyncTaskLoader<List<User>> {

    public UreportersLoader(Context context) {
        super(context);
    }

    @Override
    public List<User> loadInBackground() {
        UserRepository repository = new UserBusiness();
        User user = repository.get();
        return repository.getAllExcluding(user.getId());
    }
}
