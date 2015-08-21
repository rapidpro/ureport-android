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

    public ChatRoomHolder(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public ChatRoomHolder(ChatRoom chatRoom, ChatMembers members, ChatMessage lastMessage) {
        this.chatRoom = chatRoom;
        this.members = members;
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatRoomHolder that = (ChatRoomHolder) o;
        return chatRoom.equals(that.chatRoom);

    }

    @Override
    public int hashCode() {
        return chatRoom.hashCode();
    }
}
