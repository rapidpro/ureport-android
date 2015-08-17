package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import in.ureport.managers.UserManager;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class UreportersLoader extends AsyncTaskLoader<List<User>> {

    public UreportersLoader(Context context) {
        super(context);
    }

    @Override
    public List<User> loadInBackground() {
        return UserManager.getFakeUsers(getContext());
    }
}
