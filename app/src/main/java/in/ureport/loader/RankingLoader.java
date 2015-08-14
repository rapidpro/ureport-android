package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class RankingLoader extends AsyncTaskLoader<List<User>> {

    public RankingLoader(Context context) {
        super(context);
    }

    @Override
    public List<User> loadInBackground() {
        return UserManager.getFakeUsers(getContext());
    }

}
