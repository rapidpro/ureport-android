package in.ureport.network;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import in.ureport.managers.FirebaseManager;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class StoryServices {

    public static final String path = "story";

    public void saveStory(Story story, Firebase.CompletionListener listener) {
        User user = new User();
        user.setKey(FirebaseManager.getReference().getAuth().getUid());
        story.setUser(user);

        FirebaseManager.getReference().child(path).push().setValue(story, listener);
    }

    public void addChildEventListener(ChildEventListener childEventListener) {
        FirebaseManager.getReference().child(path).addChildEventListener(childEventListener);
    }

    public void removeChildEventListener(ChildEventListener childEventListener) {
        FirebaseManager.getReference().removeEventListener(childEventListener);
    }

    public void loadAll(ValueEventListener listener) {
        FirebaseManager.getReference().child(path).addListenerForSingleValueEvent(listener);
    }
}
