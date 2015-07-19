package in.ureport.models;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class IndividualChatRoom extends ChatRoom {

    private User friend;

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }
}
