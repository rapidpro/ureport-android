package in.ureport.listener;

import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;

/**
 * Created by johncordeiro on 16/08/15.
 */
public interface OnChatRoomSavedListener {
    void onChatRoomSaved(ChatRoom chatRoom, ChatMembers chatMembers);
}
