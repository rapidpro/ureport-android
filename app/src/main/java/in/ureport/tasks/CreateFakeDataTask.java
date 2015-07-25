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
            List<User> newUsers = saveFakeUsers();
            saveFakeStories(newUsers.toArray(new User[]{}));

            systemPreferences.setFakeDataCreated(true);
        }
        return null;
    }

    private List<User> saveFakeUsers() {
        List<User> newUsers = getNewUsers();

        UserRepository userRepository = new UserBusiness();
        userRepository.create(newUsers);
        return newUsers;
    }

    private void saveFakeStories(User... user) {
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
        user1.setPicture(context.getResources().getResourceEntryName(R.drawable.user1));
        user1.setPolls(20);
        user1.setStories(10);
        user1.setPoints(user1.getPolls() + user1.getStories());

        User user2 = new User();
        user2.setUsername("domingos_hailey123");
        user2.setEmail("domingoshailey@gmail.com");
        user2.setGender(User.Gender.Male);
        user2.setType(User.Type.Twitter);
        user2.setBirthday(new Date());
        user2.setCountry("Spain");
        user2.setPicture(context.getResources().getResourceEntryName(R.drawable.user2));
        user2.setPolls(25);
        user2.setStories(15);
        user2.setPoints(user2.getPolls() + user2.getStories());

        User user3 = new User();
        user3.setUsername("pauleenk12");
        user3.setEmail("pauleenk12@gmail.com");
        user3.setGender(User.Gender.Male);
        user3.setType(User.Type.Facebook);
        user3.setBirthday(new Date());
        user3.setCountry("Uganda");
        user3.setPicture(context.getResources().getResourceEntryName(R.drawable.user3));
        user3.setPolls(30);
        user3.setStories(20);
        user3.setPoints(user3.getPolls() + user3.getStories());

        User user4 = new User();
        user4.setUsername("robertap");
        user4.setEmail("robertap@gmail.com");
        user4.setGender(User.Gender.Female);
        user4.setType(User.Type.Facebook);
        user4.setBirthday(new Date());
        user4.setCountry("Brazil");
        user4.setPicture(context.getResources().getResourceEntryName(R.drawable.user4));
        user4.setPolls(1);
        user4.setStories(2);
        user4.setPoints(user4.getPolls() + user4.getStories());

        User user5 = new User();
        user5.setUsername("phil89");
        user5.setEmail("phil89@gmail.com");
        user5.setGender(User.Gender.Male);
        user5.setType(User.Type.Facebook);
        user5.setBirthday(new Date());
        user5.setCountry("United States");
        user5.setPicture(context.getResources().getResourceEntryName(R.drawable.user5));
        user5.setPolls(10);
        user5.setStories(28);
        user5.setPoints(user5.getPolls() + user5.getStories());

        User user6 = new User();
        user6.setUsername("joshy");
        user6.setEmail("joshy@gmail.com");
        user6.setGender(User.Gender.Male);
        user6.setType(User.Type.Ureport);
        user6.setBirthday(new Date());
        user6.setCountry("United States");
        user6.setPicture(context.getResources().getResourceEntryName(R.drawable.user6));
        user6.setPolls(5);
        user6.setStories(30);
        user6.setPoints(user6.getPolls() + user6.getStories());

        User user7 = new User();
        user7.setUsername("tommy20");
        user7.setEmail("tommy20@gmail.com");
        user7.setGender(User.Gender.Male);
        user7.setType(User.Type.Google);
        user7.setBirthday(new Date());
        user7.setCountry("Nigeria");
        user7.setPicture(context.getResources().getResourceEntryName(R.drawable.user7));
        user7.setPolls(8);
        user7.setStories(16);
        user7.setPoints(user7.getPolls() + user7.getStories());

        User user8 = new User();
        user8.setUsername("maria_gloria");
        user8.setEmail("maria_gloria@gmail.com");
        user8.setGender(User.Gender.Female);
        user8.setType(User.Type.Ureport);
        user8.setBirthday(new Date());
        user8.setCountry("Brazil");
        user8.setPicture(context.getResources().getResourceEntryName(R.drawable.user8));
        user8.setPolls(40);
        user8.setStories(100);
        user8.setPoints(user8.getPolls() + user8.getStories());

        User user9 = new User();
        user9.setUsername("easi");
        user9.setEmail("easi@gmail.com");
        user9.setGender(User.Gender.Male);
        user9.setType(User.Type.Facebook);
        user9.setBirthday(new Date());
        user9.setCountry("Nigeria");
        user9.setPicture(context.getResources().getResourceEntryName(R.drawable.user9));
        user9.setPolls(10);
        user9.setStories(1);
        user9.setPoints(user9.getPolls() + user9.getStories());

        User user10 = new User();
        user10.setUsername("gonzalez1000");
        user10.setEmail("gonzalez1000@gmail.com");
        user10.setGender(User.Gender.Male);
        user10.setType(User.Type.Google);
        user10.setBirthday(new Date());
        user10.setCountry("Mexico");
        user10.setPicture(context.getResources().getResourceEntryName(R.drawable.user10));
        user10.setPolls(10);
        user10.setStories(90);
        user10.setPoints(user10.getPolls() + user10.getStories());

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
    private List<Story> getFakeStories(User... users) {
        Story story1 = new Story();
        story1.setTitle(context.getString(R.string.story1_title));
        story1.setContent(context.getString(R.string.story1_content));
        story1.setCreatedDate(new Date());
        story1.setUser(users[0]);
        story1.setContributions(25);
        story1.setMarkers("Education");

        Story story2 = new Story();
        story2.setTitle(context.getString(R.string.story2_title));
        story2.setContent(context.getString(R.string.story2_content));
        story2.setCreatedDate(new Date());
        story2.setImage(context.getResources().getResourceEntryName(R.drawable.story2));
        story2.setUser(users[1]);
        story2.setContributions(123);
        story2.setMarkers("Water");

        Story story3 = new Story();
        story3.setTitle(context.getString(R.string.story3_title));
        story3.setContent(context.getString(R.string.story3_content));
        story2.setImage(context.getResources().getResourceEntryName(R.drawable.story3));
        story3.setCreatedDate(new Date());
        story3.setUser(users[2]);
        story3.setContributions(14);
        story3.setMarkers("Education, Politics");

        Story story4 = new Story();
        story4.setTitle(context.getString(R.string.story4_title));
        story4.setContent(context.getString(R.string.story4_content));
        story4.setCreatedDate(new Date());
        story4.setUser(users[3]);
        story4.setContributions(10);
        story4.setMarkers("Politics");

        List<Story> stories = new ArrayList<>();
        stories.add(story1);
        stories.add(story2);
        stories.add(story3);
        stories.add(story4);
        return stories;
    }
}
