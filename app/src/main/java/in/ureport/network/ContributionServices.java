package in.ureport.network;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnStoryUpdatedListener;
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
    private static final String contributionDenouncedPath = "contribution_denounced";


    public enum Type {
        Poll(pollContributionPath, pollContributionDisapprovedPath, ""),
        Story(contributionPath, contributionDisapprovedPath, contributionDenouncedPath);

        Type(String path, String disapprovedPath, String denouncedPath) {
            this.path = path;
            this.disapprovedPath = disapprovedPath;
            this.denouncedPath = denouncedPath;
        }

        private String path;
        private String disapprovedPath;
        private String denouncedPath;

    }

    private final Type type;

    public ContributionServices(Type type) {
        this.type = type;
    }

    public void saveContribution(String key, Contribution contribution, DatabaseReference.CompletionListener listener) {
        User user = new User();
        user.setKey(contribution.getAuthor().getKey());
        contribution.setAuthor(user);

        DatabaseReference object = getDefaultRoot().child(type.path).child(key).push();
        object.setValue(contribution, listener);
        contribution.setKey(object.getKey());
    }

    public void getContributionCount(Story story, final OnStoryUpdatedListener listener) {
        getDefaultRoot().child(type.path).child(story.getKey()).addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                story.setContributions(Long.valueOf(dataSnapshot.getChildrenCount()).intValue());
                listener.onStoryUpdated(story);
            }
        });
    }

    public void removeContribution(String key, Contribution contribution, DatabaseReference.CompletionListener listener) {
        getDefaultRoot().child(type.path).child(key).child(contribution.getKey()).removeValue(listener);
    }

    public void denounceContribution(String key, Contribution contribution, DatabaseReference.CompletionListener listener) {
        getDefaultRoot().child(type.denouncedPath).child(key)
                .child(contribution.getKey()).setValue(contribution, listener);
    }

    public void addChildEventListener(String key, ChildEventListener childEventListener) {
        getDefaultRoot().child(type.path).child(key).addChildEventListener(childEventListener);
    }

}
