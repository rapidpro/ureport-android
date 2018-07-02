package in.ureport.fragments;

import android.content.Context;

import in.ureport.listener.ChatRoomInterface;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;

public class NewChatFragmentHolder {

    private static ChatRoomInterface.OnChatRoomSavedListener chatRoomSavedListener;

    public static void registerChatRoomSavedListener(ChatRoomInterface.OnChatRoomSavedListener listener) {
        chatRoomSavedListener = listener;
    }

    public static void saveIndividualChatRoom(Context context, User me, User friend) {
        new ChatRoomServices().saveIndividualChatRoom(context, me, friend, new ChatRoomInterface.OnChatRoomSavedListener() {
            @Override
            public void onChatRoomSaved(ChatRoom chatRoom, ChatMembers chatMembers) {
                chatRoomSavedListener.onChatRoomSaved(chatRoom, chatMembers);
            }
        });
    }

}
