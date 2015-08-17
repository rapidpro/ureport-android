package in.ureport.models.holders;

import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;

/**
 * Created by johncordeiro on 16/08/15.
 */
public class ChatRoomHolder {

    public ChatRoom chatRoom;

    public ChatMembers members;

    public ChatMessage lastMessage;

    public ChatRoomHolder(ChatRoom chatRoom, ChatMembers members, ChatMessage lastMessage) {
        this.chatRoom = chatRoom;
        this.members = members;
        this.lastMessage = lastMessage;
    }
}
