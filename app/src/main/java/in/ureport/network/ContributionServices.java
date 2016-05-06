package in.ureport.network;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnStoryContributionCountListener;
import in.ureport.models.Contribution;
import in.ureport.models.Story;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 14/08/15.
 */
public class ContributionServices extends ProgramServices {

    private static final String pollContributionPath = "poll_contribution";
    private static final String pollContributionDisapprovedPath = "poll_contribution_disapproved";

    private static final String contributionPath = "contribution";
    private static final String contributionDisapprovedPath = "contribution_disapproved";


    public enum Type {
        Poll(pollContributionPath, pollContributionDisapprovedPath),
        Story(contributionPath, contributionDisapprovedPath);

        Type(String path, String disapprovedPath) {
            this.path = path;
            this.disapprovedPath = disapprovedPath;
        }

        private String path;
        private String disapprovedPath;

    }

    private final Type type;

    public ContributionServices(Type type) {
        this.type = type;
    }

    public void saveContribution(String key, Contribution contribution, Firebase.CompletionListener listener) {
        User user = new User();
        user.setKey(contribution.getAuthor().getKey());
        contribution.setAuthor(user);

        Firebase object = getDefaultRoot().child(type.path).child(key).push();
        object.setValue(contribution, listener);
        contribution.setKey(object.getKey());
    }

    public void getContributionCount(Story story, final OnStoryContributionCountListener listener) {
        getDefaultRoot().child(type.path).child(story.getKey()).addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                listener.onStoryContributionCountListener(dataSnapshot.getChildrenCount());
            }
        });
    }
    
    public void removeContribution(String key, Contribution contribution, Firebase.CompletionListener listener) {
        getDefaultRoot().child(type.path).child(key).child(contribution.getKey()).removeValue();
        getDefaultRoot().child(type.disapprovedPath).child(key)
                .child(contribution.getKey()).setValue(contribution, listener);
    }

    public void addChildEventListener(String key, ChildEventListener childEventListener) {
        getDefaultRoot().child(type.path).child(key).addChildEventListener(childEventListener);
    }
    
}
