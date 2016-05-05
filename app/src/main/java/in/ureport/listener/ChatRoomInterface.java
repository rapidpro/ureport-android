package in.ureport.listener;

import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;

/**
 * Created by johncordeiro on 18/09/15.
 */
public interface ChatRoomInterface {

    interface OnChatLastMessageLoadedListener {
        void onChatLastMessageLoaded(ChatRoom chatRoom, ChatMembers chatMembers, ChatMessage chatMessage);
    }

    interface OnChatMembersLoadedListener {
        void onChatMembersLoaded(ChatMembers chatMembers);
    }

    interface OnChatRoomLoadedListener {
        void onChatRoomLoaded(ChatRoom chatRoom, ChatMembers chatMembers);
    }

    interface OnChatRoomSavedListener {
        void onChatRoomSaved(ChatRoom chatRoom, ChatMembers chatMembers);
    }
}
