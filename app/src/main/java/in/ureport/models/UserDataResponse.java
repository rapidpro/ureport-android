package in.ureport.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class UserDataResponse {

    public User user;
    public List<Chat> chats;
    public Stories stories;
    public Contributions contributions;

    public class Chat {
        @SerializedName("chatType")
        public String type;
        public List<String> messages;
    }

    public class Stories {
        public List<Story> publishedStories;
        public List<Story> likedStories;
        public List<Story> storiesInModeration;
        public List<Story> disapprovedStories;
    }

    public class Contributions {
        public List<Contribution> storyContributions;
        public List<Contribution> pollContributions;
    }

    public class Contribution {
        @SerializedName(value = "storyTitle", alternate = {"pollTitle"})
        public String title;
        public String contribution;
        public Long createdDate;
    }

}
