package in.ureport.managers;

import java.util.ArrayList;
import java.util.List;

import in.ureport.listener.ChatRoomInterface;
import in.ureport.listener.OnChatRoomCheckUserListener;
import in.ureport.models.ChatMembers;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;

/**
 * Created by johncordeiro on 18/09/15.
 */
public class ChatRoomValidator {

    public void checkIfExistsChatRoom(User me, final User friend, final OnChatRoomCheckUserListener listener) {
        if(me.getChatRooms() != null) {
            ChatRoomServices chatRoomServices = new ChatRoomServices();

            final List<String> chatRoomKeys = new ArrayList<>(me.getChatRooms().keySet());
            final List<ChatMembers> chatMembersList = new ArrayList<>();

            for (int position = 0; position < chatRoomKeys.size(); position++) {
                String chatRoomKey = chatRoomKeys.get(position);
                chatRoomServices.loadChatRoomMembersWithoutData(chatRoomKey, new ChatRoomInterface.OnChatMembersLoadedListener() {
                    @Override
                    public void onChatMembersLoaded(ChatMembers chatMembers) {
                        chatMembersList.add(chatMembers);
                        if(chatMembersList.size() == chatRoomKeys.size()) {
                            checkIfContainsFriend(chatMembersList, friend, listener);
                        }
                    }
                });
            }
        } else {
            listener.onChatRoomCheckUser(false);
        }
    }

    private void checkIfContainsFriend(List<ChatMembers> chatMembersList, User friend
            , OnChatRoomCheckUserListener listener) {
        boolean userChatMember = false;

        for (ChatMembers chatMembers : chatMembersList) {
            if (isUserChatMember(friend, chatMembers)) {
                userChatMember = true;
                listener.onChatRoomCheckUser(true);
                break;
            }
        }

        if(!userChatMember) listener.onChatRoomCheckUser(false);
    }

    private boolean isUserChatMember(User friend, ChatMembers chatMembers) {
        List<User> users = chatMembers.getUsers();
        return users != null && users.size() == 2 && users.contains(friend);
    }

}
