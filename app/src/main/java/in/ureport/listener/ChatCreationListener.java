package in.ureport.listener;

/**
 * Created by johncordeiro on 19/07/15.
 */
public interface ChatCreationListener {
    void onCreateGroupChatCalled();
    void onCreateIndividualChatCalled();
    void onChatRoomCreated();
}
