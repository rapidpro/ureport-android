package in.ureport.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.ureport.R;
import in.ureport.activities.ChatRoomActivity;
import in.ureport.activities.InviteContactsActivity;
import in.ureport.listener.ChatRoomInterface;
import in.ureport.listener.OnSeeOpenGroupsListener;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.LocalNotificationManager;
import in.ureport.managers.SearchManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;
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
public class ListChatRoomsFragment extends Fragment implements ChatRoomsAdapter.OnChatRoomSelectedListener
    , SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String TAG = "ListChatRoomsFragment";

    private static final int REQUEST_CODE_CHAT_ROOM = 100;
    public static final String EXTRA_RESULT_CHAT_ROOM = "chatRoom";

    private ChatRoomServices chatRoomServices;
    private ChatRoomsAdapter chatRoomsAdapter;

    private UserServices userServices;

    private Map<ChatRoom, Integer> chatNotifications;
    private RecyclerView chatsList;

    private OnSeeOpenGroupsListener onSeeOpenGroupsListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_chat_rooms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        setupObjects();
        setupView(view);
        loadData();
        cancelChatNotifications();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && chatsList != null) chatsList.setAdapter(chatRoomsAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.menu_chat_rooms, menu);

        SearchManager searchManager = new SearchManager(getActivity());
        searchManager.addSearchView(menu, R.string.hint_search_chat_rooms, this, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSeeOpenGroupsListener) {
            onSeeOpenGroupsListener = (OnSeeOpenGroupsListener) context;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.inviteContacts:
                startInviteContacts();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startInviteContacts() {
        Intent inviteContactsIntent = new Intent(getActivity(), InviteContactsActivity.class);
        startActivity(inviteContactsIntent);
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
                chatRoomForUpdate.chatRoom = data.getParcelableExtra(ChatRoomActivity.EXTRA_CHAT_ROOM);
                chatRoomForUpdate.members = data.getParcelableExtra(ChatRoomActivity.EXTRA_CHAT_MEMBERS);
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
        chatsList = (RecyclerView) view.findViewById(R.id.chatsList);
        chatsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatsList.addItemDecoration(new DividerItemDecoration(getActivity()));

        chatRoomsAdapter = new ChatRoomsAdapter();
        chatRoomsAdapter.setOnChatRoomSelectedListener(this);
        chatsList.setAdapter(chatRoomsAdapter);

        Button seeOpenGroups = (Button) view.findViewById(R.id.seeOpenGroups);
        seeOpenGroups.setOnClickListener(onSeeOpenGroups);
    }

    private void loadData() {
        GetUnreadMessagesTask getUnreadMessagesTask = new GetUnreadMessagesTask() {
            @Override
            protected void onPostExecute(Map<ChatRoom, Integer> chatNotifications) {
                super.onPostExecute(chatNotifications);
                ListChatRoomsFragment.this.chatNotifications = chatNotifications;
                userServices.addChildEventListenerForChatRooms(UserManager.getUserId(), childEventListener);
            }
        };
        getUnreadMessagesTask.execute();
    }

    private View.OnClickListener onSeeOpenGroups = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(onSeeOpenGroupsListener != null){
                onSeeOpenGroupsListener.onSeeOpenGroups();
            }
        }
    };

    private ChildEventListenerAdapter childEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
            super.onChildAdded(dataSnapshot, previousChild);

            String chatRoomKey = dataSnapshot.getKey();
            chatRoomServices.getChatRoom(chatRoomKey, new ChatRoomInterface.OnChatRoomLoadedListener() {
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

            ChatRoom chatRoom = new ChatRoom(){};
            chatRoom.setKey(dataSnapshot.getKey());

            chatRoomsAdapter.removeChatRoom(new ChatRoomHolder(chatRoom));
        }
    };

    public void startChatRoom(ChatRoom chatRoom) {
        Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM_KEY, chatRoom.getKey());
        startActivityForResult(chatRoomIntent, REQUEST_CODE_CHAT_ROOM);
    }

    @Override
    public void onChatRoomSelected(ChatRoom chatRoom, ChatMembers members, Pair<View, String>... views) {
        Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM, chatRoom);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_MEMBERS, members);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), views);
        ActivityCompat.startActivityForResult(getActivity(), chatRoomIntent, REQUEST_CODE_CHAT_ROOM, options.toBundle());
    }

    @Override
    public boolean onClose() {
        chatsList.setAdapter(chatRoomsAdapter);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        updateRoomsForQuery(query);
        return false;
    }

    private void updateRoomsForQuery(String query) {
        try {
            List<ChatRoomHolder> chatRooms = getChatRoomsListForQuery(query);

            ChatRoomsAdapter chatRoomsAdapter = new ChatRoomsAdapter();
            chatRoomsAdapter.updateData(chatRooms);
            chatRoomsAdapter.setOnChatRoomSelectedListener(this);
            chatsList.setAdapter(chatRoomsAdapter);
        } catch(Exception exception) {
            Log.e(TAG, "updateRoomsForQuery ", exception);
        }
    }

    @NonNull
    private List<ChatRoomHolder> getChatRoomsListForQuery(String query) {
        List<ChatRoomHolder> chatRooms = new ArrayList<>();

        query = query.toLowerCase();
        for (ChatRoomHolder chatRoomHolder : chatRoomsAdapter.getChatRooms()) {
            if (chatRoomHolder.chatRoom.getType() == ChatRoom.Type.Group) {
                GroupChatRoom groupChatRoom = (GroupChatRoom) chatRoomHolder.chatRoom;
                if (groupChatRoom.getTitle().toLowerCase().contains(query)) {
                    chatRooms.add(chatRoomHolder);
                }
            } else {
                for (User user : chatRoomHolder.members.getUsers()) {
                    if (user.getNickname().toLowerCase().contains(query)) {
                        chatRooms.add(chatRoomHolder);
                    }
                }
            }
        }
        return chatRooms;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public List<ChatRoomHolder> getChatRooms() {
        return chatRoomsAdapter.getChatRooms();
    }
}
