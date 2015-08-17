package in.ureport.listener;

import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;

/**
 * Created by johncordeiro on 16/08/15.
 */
public interface OnChatRoomLoadedListener {

    void onChatRoomLoaded(ChatRoom chatRoom, ChatMembers chatMembers, ChatMessage lastMessage);

}
