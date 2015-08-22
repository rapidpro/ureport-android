package in.ureport.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;

import java.util.Map;

import in.ureport.R;
import in.ureport.activities.ChatRoomActivity;
import in.ureport.listener.OnChatRoomLoadedListener;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.LocalNotificationManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.holders.ChatRoomHolder;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.helpers.DividerItemDecoration;
import in.ureport.tasks.GetUnreadMessagesTask;
import in.ureport.views.adapters.ChatRoomsAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ListChatRoomsFragment extends Fragment implements ChatRoomsAdapter.OnChatRoomSelectedListener {

    private static final int REQUEST_CODE_CHAT_ROOM = 100;
    public static final String EXTRA_RESULT_CHAT_ROOM = "chatRoom";

    private ChatRoomServices chatRoomServices;
    private ChatRoomsAdapter chatRoomsAdapter;

    private UserServices userServices;

    private Map<ChatRoom, Integer> chatNotifications;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_chat_rooms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObjects();
        setupView(view);
        loadData();
        cancelChatNotifications();
    }

    private void cancelChatNotifications() {
        LocalNotificationManager notifcationManager = new LocalNotificationManager(getActivity());
        notifcationManager.cancelChatNotification();
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
        if(chatRoom != null && chatRoomsAdapter != null && chatRoomsAdapter.getChatRooms() != null) {
            ChatRoomHolder holder = new ChatRoomHolder(chatRoom);
            int chatRoomIndex = chatRoomsAdapter.getChatRooms().indexOf(holder);

            if(chatRoomIndex >= 0) {
                ChatRoomHolder chatRoomForUpdate = chatRoomsAdapter.getChatRooms().get(chatRoomIndex);
                chatRoomForUpdate.chatRoom.setUnreadMessages(0);

                chatRoomsAdapter.notifyItemChanged(chatRoomIndex);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseManager.getReference().removeEventListener(childEventListener);
    }

    private void setupObjects() {
        chatRoomServices = new ChatRoomServices();
        userServices = new UserServices();
    }

    private void setupView(View view) {
        RecyclerView chatsList = (RecyclerView) view.findViewById(R.id.chatsList);
        chatsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatsList.addItemDecoration(new DividerItemDecoration(getActivity()));

        chatRoomsAdapter = new ChatRoomsAdapter();
        chatRoomsAdapter.setOnChatRoomSelectedListener(this);
        chatsList.setAdapter(chatRoomsAdapter);
    }

    private void loadData() {
        GetUnreadMessagesTask getUnreadMessagesTask = new GetUnreadMessagesTask() {
            @Override
            protected void onPostExecute(Map<ChatRoom, Integer> chatNotifications) {
                super.onPostExecute(chatNotifications);
                ListChatRoomsFragment.this.chatNotifications = chatNotifications;
                userServices.addChildEventListenerForChatRooms(FirebaseManager.getAuthUserKey(), childEventListener);
            }
        };
        getUnreadMessagesTask.execute();
    }

    private ChildEventListenerAdapter childEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
            super.onChildAdded(dataSnapshot, previousChild);
            String chatRoomKey = dataSnapshot.getKey();

            chatRoomServices.getChatRoom(chatRoomKey, new OnChatRoomLoadedListener() {
                @Override
                public void onChatRoomLoaded(ChatRoom chatRoom, ChatMembers chatMembers, ChatMessage lastMessage) {
                    chatRoom.setUnreadMessages(chatNotifications.get(chatRoom));
                    chatRoomsAdapter.addChatRoom(new ChatRoomHolder(chatRoom, chatMembers, lastMessage));
                }
            });
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            super.onChildRemoved(dataSnapshot);
            String chatRoomKey = dataSnapshot.getKey();

            IndividualChatRoom chatRoom = new IndividualChatRoom();
            chatRoom.setKey(chatRoomKey);

            chatRoomsAdapter.removeChatRoom(new ChatRoomHolder(chatRoom));
        }
    };

    @Override
    public void onChatRoomSelected(ChatRoom chatRoom, ChatMembers members) {
        Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM, chatRoom);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_MEMBERS, members);
        startActivityForResult(chatRoomIntent, REQUEST_CODE_CHAT_ROOM);
    }
}
