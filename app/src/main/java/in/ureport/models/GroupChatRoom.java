package in.ureport.models;

import java.util.List;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class GroupChatRoom extends ChatRoom {

    private ChatGroup chatGroup;

    private List<User> participants;

    public ChatGroup getChatGroup() {
        return chatGroup;
    }

    public void setChatGroup(ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }
}
