package in.ureport.network;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnStoryContributionCountListener;
import in.ureport.managers.FirebaseManager;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class ContributionServices extends ProgramServices {

    public static final String path = "contribution";

    public void saveContribution(Story story, Contribution contribution, Firebase.CompletionListener listener) {
        User user = new User();
        user.setKey(contribution.getAuthor().getKey());
        contribution.setAuthor(user);

        Firebase object = getDefaultRoot().child(path).child(story.getKey()).push();
        object.setValue(contribution, listener);
        contribution.setKey(object.getKey());
    }

    public void getContributionCount(Story story, final OnStoryContributionCountListener listener) {
        getDefaultRoot().child(path).child(story.getKey()).addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                listener.onStoryContributionCountListener(dataSnapshot.getChildrenCount());
            }
        });
    }

    public void addChildEventListener(Story story, ChildEventListener childEventListener) {
        getDefaultRoot().child(path).child(story.getKey()).addChildEventListener(childEventListener);
    }
    
}
