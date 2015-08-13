package in.ureport.tasks;

import android.os.AsyncTask;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class GetUserLoggedTask extends AsyncTask<Void, Void, User> {

    @Override
    protected User doInBackground(Void... voids) {
        UserRepository userRepository = new UserBusiness();
        return userRepository.get();
    }

}
