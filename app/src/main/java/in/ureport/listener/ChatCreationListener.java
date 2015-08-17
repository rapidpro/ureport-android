package in.ureport.listener;

import in.ureport.models.User;

/**
 * Created by johncordeiro on 19/07/15.
 */
public interface ChatCreationListener {
    void onCreateGroupChatCalled();
    void onCreateIndividualChatCalled(User user);
}
