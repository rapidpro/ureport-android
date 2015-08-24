package in.ureport.listener;

import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;

/**
 * Created by johncordeiro on 21/08/15.
 */
public interface InfoGroupChatListener {

    void onEditGroupChat(ChatRoom chatRoom, ChatMembers members);
    void onChatRoomClose(ChatRoom chatRoom, ChatMembers members);
    void onChatRoomLeave(ChatRoom chatRoom);

}
