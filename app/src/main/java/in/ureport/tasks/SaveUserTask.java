package in.ureport.tasks;

import android.os.AsyncTask;

import in.ureport.models.User;

/**
 * Created by ilhasoft on 7/9/15.
 */
public class SaveUserTask extends AsyncTask<User, Void, Boolean> {

    @Override
    protected Boolean doInBackground(User... users) {
        if(users.length == 0) return false;

        for (User user : users) {
            user.save();
        }

        return true;
    }

}
