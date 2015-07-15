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
            saveFakeStories(users[0]);
            saveFakeUsers();

            systemPreferences.setFakeDataCreated(true);
        }
        return null;
    }

    private void saveFakeUsers() {
        List<User> newUsers = getNewUsers();

        UserRepository userRepository = new UserBusiness();
        userRepository.create(newUsers);
    }

    private void saveFakeStories(User user) {
        List<Story> stories = getFakeStories(user);

        StoryRepository storyRepository = new StoryBusiness();
        storyRepository.create(stories);
    }

    @NonNull
    private List<User> getNewUsers() {
        User user1 = new User();
        user1.setUsername("esther_aiken");
        user1.setEmail("estheraiken@gmail.com");
        user1.setGender(User.Gender.Female);
        user1.setType(User.Type.Twitter);
        user1.setBirthday(new Date());
        user1.setCountry("Uganda");

        User user2 = new User();
        user2.setUsername("domingos_hailey123");
        user2.setEmail("domingoshailey@gmail.com");
        user2.setGender(User.Gender.Male);
        user2.setType(User.Type.Twitter);
        user2.setBirthday(new Date());
        user2.setCountry("Spain");

        User user3 = new User();
        user3.setUsername("pauleenk12");
        user3.setEmail("pauleenk12@gmail.com");
        user3.setGender(User.Gender.Male);
        user3.setType(User.Type.Facebook);
        user3.setBirthday(new Date());
        user3.setCountry("Uganda");

        User user4 = new User();
        user4.setUsername("robertap");
        user4.setEmail("robertap@gmail.com");
        user4.setGender(User.Gender.Female);
        user4.setType(User.Type.Facebook);
        user4.setBirthday(new Date());
        user4.setCountry("Brazil");

        User user5 = new User();
        user5.setUsername("phil89");
        user5.setEmail("phil89@gmail.com");
        user5.setGender(User.Gender.Male);
        user5.setType(User.Type.Facebook);
        user5.setBirthday(new Date());
        user5.setCountry("United States");

        User user6 = new User();
        user6.setUsername("joshy");
        user6.setEmail("joshy@gmail.com");
        user6.setGender(User.Gender.Male);
        user6.setType(User.Type.Ureport);
        user6.setBirthday(new Date());
        user6.setCountry("United States");

        User user7 = new User();
        user7.setUsername("tommy20");
        user7.setEmail("tommy20@gmail.com");
        user7.setGender(User.Gender.Male);
        user7.setType(User.Type.Google);
        user7.setBirthday(new Date());
        user7.setCountry("Nigeria");

        User user8 = new User();
        user8.setUsername("maria_gloria");
        user8.setEmail("maria_gloria@gmail.com");
        user8.setGender(User.Gender.Female);
        user8.setType(User.Type.Ureport);
        user8.setBirthday(new Date());
        user8.setCountry("Brazil");

        User user9 = new User();
        user9.setUsername("carvajal");
        user9.setEmail("carvajal@gmail.com");
        user9.setGender(User.Gender.Male);
        user9.setType(User.Type.Facebook);
        user9.setBirthday(new Date());
        user9.setCountry("Spain");

        User user10 = new User();
        user10.setUsername("gonzalez1000");
        user10.setEmail("gonzalez1000@gmail.com");
        user10.setGender(User.Gender.Male);
        user10.setType(User.Type.Google);
        user10.setBirthday(new Date());
        user10.setCountry("Mexico");

        List<User> newUsers = new ArrayList<>();
        newUsers.add(user1);
        newUsers.add(user2);
        newUsers.add(user3);
        newUsers.add(user4);
        newUsers.add(user5);
        newUsers.add(user6);
        newUsers.add(user7);
        newUsers.add(user8);
        newUsers.add(user9);
        newUsers.add(user10);
        return newUsers;
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
