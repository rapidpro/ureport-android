package in.ureport.network;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import in.ureport.managers.FirebaseManager;
import in.ureport.managers.UserManager;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class StoryServices extends ProgramServices {

    public static final String path = "story";

    public void saveStory(Story story, Firebase.CompletionListener listener) {
        story.setUser(UserManager.getUserId());

        getDefaultRoot().child(path).push().setValue(story, listener);
    }

    public void addChildEventListenerForUser(User user, ChildEventListener childEventListener) {
        getCountryProgram().child(user.getCountryProgram()).child(path)
                .orderByChild("user").equalTo(user.getKey()).addChildEventListener(childEventListener);
    }

    public void addChildEventListener(ChildEventListener childEventListener) {
        getDefaultRoot().child(path).addChildEventListener(childEventListener);
    }

    public void removeChildEventListener(ChildEventListener childEventListener) {
        getDefaultRoot().child(path).removeEventListener(childEventListener);
    }
}
