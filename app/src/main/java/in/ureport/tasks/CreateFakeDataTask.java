package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.ureport.R;
import in.ureport.db.business.StoryBusiness;
import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.StoryRepository;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateFakeDataTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    public CreateFakeDataTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
        SystemPreferences systemPreferences = new SystemPreferences(context);
        if(!systemPreferences.isFakeDataCreated()) {
//            List<User> newUsers = saveFakeUsers();
//            saveFakeStories(newUsers.toArray(new User[]{}));

//            systemPreferences.setFakeDataCreated(true);
        }
        return null;
    }

}
