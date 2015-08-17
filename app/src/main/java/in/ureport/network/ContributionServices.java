package in.ureport.network;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;

import in.ureport.managers.FirebaseManager;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class ContributionServices {

    public static final String path = "contribution";

    public void saveContribution(Story story, Contribution contribution, Firebase.CompletionListener listener) {
        User user = new User();
        user.setKey(contribution.getAuthor().getKey());
        contribution.setAuthor(user);

        Firebase object = FirebaseManager.getReference().child(path).child(story.getKey()).push();
        object.setValue(contribution, listener);
        contribution.setKey(object.getKey());
    }

    public void addChildEventListener(Story story, ChildEventListener childEventListener) {
        FirebaseManager.getReference().child(path).child(story.getKey()).addChildEventListener(childEventListener);
    }
    
}
