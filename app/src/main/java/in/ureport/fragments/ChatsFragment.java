package in.ureport.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.ureport.R;
import in.ureport.activities.ChatRoomActivity;
import in.ureport.activities.GroupInfoActivity;
import in.ureport.listener.InfoGroupChatListener;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.holders.ChatRoomHolder;
import in.ureport.views.adapters.ChatRoomsAdapter;

/**
 * Created by john-mac on 4/27/16.
 */
public class ChatsFragment extends Fragment implements ChatRoomsAdapter.OnChatRoomSelectedListener,
        ChatRoomFragment.ChatRoomListener, InfoGroupChatListener {

    public static final String EXTRA_RESULT_CHAT_ROOM = "chatRoom";

    private static final int REQUEST_CODE_CHAT_ROOM = 100;
    public static final int REQUEST_CODE_GROUP_INFO = 500;

    private View chatRoomContainer;

    private ListChatRoomsFragment listChatRoomsFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        setupView(view);
        selectFirstItemIfNeeded();
    }

    private void selectFirstItemIfNeeded() {
        if(chatRoomContainer != null) {
            ChatRoomFragment chatRoomContainer = (ChatRoomFragment) getChildFragmentManager().findFragmentById(R.id.chatRoomContainer);
            if(chatRoomContainer == null) {
                listChatRoomsFragment.setSelectFirst(true);
            } else {
                listChatRoomsFragment.selectChatRoom(chatRoomContainer.getChatRoom());
            }
        } else {
            listChatRoomsFragment.setSelectFirst(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setupView(View view) {
        listChatRoomsFragment = (ListChatRoomsFragment) getChildFragmentManager().findFragmentById(R.id.chatRoomsFragment);
        listChatRoomsFragment.setOnChatRoomSelectedListener(this);

        chatRoomContainer = view.findViewById(R.id.chatRoomContainer);

        ChatRoomFragment chatRoomFragment = (ChatRoomFragment) getChildFragmentManager().findFragmentById(R.id.chatRoomContainer);
        if(chatRoomFragment != null) {
            chatRoomFragment.setInfoGroupChatListener(this);
            chatRoomFragment.setChatRoomListener(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case REQUEST_CODE_CHAT_ROOM:
                    updateUnreadMessages(data);
            }
        }
    }

    private void updateUnreadMessages(Intent data) {
        ChatRoom chatRoom = data.getParcelableExtra(EXTRA_RESULT_CHAT_ROOM);
        if(chatRoom != null && getChatRooms() != null) {
            ChatRoomHolder holder = new ChatRoomHolder(chatRoom);
            int chatRoomIndex = getChatRooms().indexOf(holder);

            if(chatRoomIndex >= 0) {
                ChatRoomHolder chatRoomForUpdate = getChatRooms().get(chatRoomIndex);
                chatRoomForUpdate.chatRoom = data.getParcelableExtra(ChatRoomActivity.EXTRA_CHAT_ROOM);
                chatRoomForUpdate.members = data.getParcelableExtra(ChatRoomActivity.EXTRA_CHAT_MEMBERS);
                chatRoomForUpdate.chatRoom.setUnreadMessages(0);

                listChatRoomsFragment.notifyItemChanged(chatRoomIndex);
            }
        }
    }

    private static final String TAG = "ChatsFragment";

    public List<ChatRoomHolder> getChatRooms() {
        Log.d(TAG, "getChatRooms() returned: " + listChatRoomsFragment);
        return listChatRoomsFragment.getChatRooms();
    }

    @Override
    public void onChatRoomSelected(ChatRoom chatRoom, ChatMembers members) {
        if(chatRoomContainer != null) {
            listChatRoomsFragment.setSelectable(true);

            ChatRoomFragment chatRoomFragment = ChatRoomFragment.newInstance(chatRoom, members, true);
            chatRoomFragment.setChatRoomListener(this);
            chatRoomFragment.setInfoGroupChatListener(this);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.chatRoomContainer, chatRoomFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else {
            listChatRoomsFragment.setSelectable(false);
            startChatRoomWithMembers(chatRoom, members);
        }
    }

    public void startChatRoom(ChatRoom chatRoom) {
        Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM_KEY, chatRoom.getKey());
        startActivityForResult(chatRoomIntent, REQUEST_CODE_CHAT_ROOM);
    }

    private void startChatRoomWithMembers(ChatRoom chatRoom, ChatMembers members) {
        Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM, chatRoom);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_MEMBERS, members);
        startActivityForResult(chatRoomIntent, REQUEST_CODE_CHAT_ROOM);
    }

    @Override
    public void onEditGroupChat(ChatRoom chatRoom, ChatMembers members) {}

    @Override
    public void onChatRoomClose(ChatRoom chatRoom, ChatMembers members) {}

    @Override
    public void onChatRoomLeave(ChatRoom chatRoom) {
        UserManager.leaveFromGroup(getActivity(), chatRoom, () -> Log.i(TAG, "onChatRoomLeave: "));
    }

    @Override
    public void onChatRoomInfoView(ChatRoom chatRoom, ChatMembers chatMembers, Pair<View, String>... pairs) {
        Intent groupInfoIntent = new Intent(getActivity(), GroupInfoActivity.class);
        groupInfoIntent.putExtra(GroupInfoActivity.EXTRA_CHAT_ROOM, chatRoom);
        groupInfoIntent.putExtra(GroupInfoActivity.EXTRA_CHAT_MEMBERS, chatMembers);
        startActivityForResult(groupInfoIntent, REQUEST_CODE_GROUP_INFO);
    }
}
