package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.ilhasoft.support.db.business.AbstractBusiness;
import br.com.ilhasoft.support.db.repository.AbstractRepository;
import in.ureport.R;
import in.ureport.db.business.UserBusiness;
import in.ureport.db.repository.UserRepository;
import in.ureport.models.Story;
import in.ureport.models.User;
import in.ureport.pref.SystemPreferences;

/**
 * Created by johncordeiro on 7/14/15.
 */
public class CreateFakeDataTask extends AsyncTask<User, Void, Void> {

    private Context context;

    public CreateFakeDataTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(User... users) {
        if(users.length == 0) return null;

        SystemPreferences systemPreferences = new SystemPreferences(context);
        if(!systemPreferences.isFakeDataCreated()) {
            List<Story> stories = getFakeStories(users[0]);

            AbstractRepository<Story> storyRepository = new AbstractBusiness<>(Story.class);
            storyRepository.create(stories);

            systemPreferences.setFakeDataCreated(true);
        }
        return null;
    }

    @NonNull
    private List<Story> getFakeStories(User user) {
        Story story1 = new Story();
        story1.setTitle(context.getString(R.string.story1_title));
        story1.setContent(context.getString(R.string.story1_content));
        story1.setCreatedDate(new Date());
        story1.setUser(user);
        story1.setContributions(25);
        story1.setCoauthors(2);

        Story story2 = new Story();
        story2.setTitle(context.getString(R.string.story2_title));
        story2.setContent(context.getString(R.string.story2_content));
        story2.setCreatedDate(new Date());
        story2.setUser(user);
        story2.setContributions(123);
        story2.setCoauthors(5);

        Story story3 = new Story();
        story3.setTitle(context.getString(R.string.story3_title));
        story3.setContent(context.getString(R.string.story3_content));
        story3.setCreatedDate(new Date());
        story3.setUser(user);
        story3.setContributions(14);
        story3.setCoauthors(10);

        Story story4 = new Story();
        story4.setTitle(context.getString(R.string.story4_title));
        story4.setContent(context.getString(R.string.story4_content));
        story4.setCreatedDate(new Date());
        story4.setUser(user);
        story4.setContributions(10);
        story4.setCoauthors(8);

        List<Story> stories = new ArrayList<>();
        stories.add(story1);
        stories.add(story2);
        stories.add(story3);
        stories.add(story4);
        return stories;
    }
}
