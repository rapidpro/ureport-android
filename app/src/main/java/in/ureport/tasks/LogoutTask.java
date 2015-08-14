package in.ureport.tasks;

import android.os.AsyncTask;

import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class LogoutTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        UserRepository repository = new UserBusiness();
        repository.deleteAll();
        return null;
    }

}
