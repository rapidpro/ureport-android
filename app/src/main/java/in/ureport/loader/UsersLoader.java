package in.ureport.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class UsersLoader extends AsyncTaskLoader<List<User>> {

    public UsersLoader(Context context) {
        super(context);
    }

    @Override
    public List<User> loadInBackground() {
        UserRepository repository = new UserBusiness();
        return repository.getAllOrdered();
    }

}
