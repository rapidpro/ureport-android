package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;

import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;

/**
 * Created by ilhasoft on 7/9/15.
 */
public class SaveUserTask extends AsyncTask<User, Void, Boolean> {

    private Context context;

    public SaveUserTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(User... users) {
        if(users.length == 0) return false;

        User user = users[0];
        Long id = user.save();

        SystemPreferences systemPreferences = new SystemPreferences(context);
        systemPreferences.setUserLoggedId(id);

        return true;
    }

}
