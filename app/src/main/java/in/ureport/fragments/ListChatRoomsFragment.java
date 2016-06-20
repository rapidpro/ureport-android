package in.ureport.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.ureport.R;
import in.ureport.activities.ChatCreationActivity;
import in.ureport.activities.InviteContactsActivity;
import in.ureport.activities.MainActivity;
import in.ureport.helpers.ValueEventListenerAdapter;
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
import in.ureport.helpers.DividerItemDecoration;
import in.ureport.tasks.GetUnreadMessagesTask;
import in.ureport.views.adapters.ChatRoomsAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ListChatRoomsFragment extends Fragment implements SearchView.OnQueryTextListener
        , SearchView.OnCloseListener {

    private static final String TAG = "ListChatRoomsFragment";

    private ChatRoomServices chatRoomServices;
    private ChatRoomsAdapter chatRoomsAdapter;

    private UserServices userServices;

    private Map<ChatRoom, Integer> chatNotifications;
    private RecyclerView chatsList;

    private List<ChildEventListener> chatEventListenerList;

    private OnSeeOpenGroupsListener onSeeOpenGroupsListener;
    private ChatRoomsAdapter.OnChatRoomSelectedListener onChatRoomSelectedListener;

    private boolean selectFirst = false;
    private ChatRoom selectableChatRoom;

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
        if (getParentFragment().isMenuVisible()) {
            menu.clear();

            inflater.inflate(R.menu.menu_chat_rooms, menu);

            SearchManager searchManager = new SearchManager(getActivity());
            searchManager.addSearchView(menu, R.string.hint_search_chat_rooms, this, this);
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseManager.getReference().removeEventListener(valueListener);
        for (ChildEventListener chatEventListener : chatEventListenerList) {
            FirebaseManager.getReference().removeEventListener(chatEventListener);
        }
    }

    private void setupObjects() {
        chatEventListenerList = new ArrayList<>();
        chatRoomServices = new ChatRoomServices();
        userServices = new UserServices();
    }

    private void setupView(View view) {
        chatsList = (RecyclerView) view.findViewById(R.id.chatsList);
        chatsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatsList.addItemDecoration(new DividerItemDecoration(getActivity()));

        chatRoomsAdapter = new ChatRoomsAdapter();
        chatsList.setAdapter(chatRoomsAdapter);

        Button seeOpenGroups = (Button) view.findViewById(R.id.seeOpenGroups);
        seeOpenGroups.setOnClickListener(onSeeOpenGroups);

        FloatingActionButton createChatRoom = (FloatingActionButton) view.findViewById(R.id.createChatRoom);
        createChatRoom.setOnClickListener(onCreateChatRoomListener);
    }

    private void loadData() {
        GetUnreadMessagesTask getUnreadMessagesTask = new GetUnreadMessagesTask() {
            @Override
            protected void onPostExecute(Map<ChatRoom, Integer> chatNotifications) {
                super.onPostExecute(chatNotifications);
                ListChatRoomsFragment.this.chatNotifications = chatNotifications;
                userServices.addValueEventListenerForChatRooms(UserManager.getUserId(), valueListener);
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

    private ValueEventListener valueListener = new ValueEventListenerAdapter() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            super.onDataChange(dataSnapshot);
            chatRoomsAdapter.getChatRooms().beginBatchedUpdates();

            List<ChatRoomHolder> chatRoomHolders = new ArrayList<>();

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String chatRoomKey = snapshot.getKey();
                chatRoomServices.getChatRoom(chatRoomKey, new ChatRoomInterface.OnChatRoomLoadedListener() {
                    @Override
                    public void onChatRoomLoadFailed() {}
                    @Override
                    public void onChatRoomLoaded(ChatRoom chatRoom, ChatMembers chatMembers) {
                        ChatRoomHolder holder = new ChatRoomHolder(chatRoom, chatMembers, null);
                        chatRoom.setUnreadMessages(chatNotifications.get(chatRoom));
                        chatRoomHolders.add(holder);

                        updateChatRoomsWithLastMessage(holder);
                        onUpdatedChatRooms(chatRoomHolders);
                    }
                });
            }
        }
    };

    public void setOnChatRoomSelectedListener(ChatRoomsAdapter.OnChatRoomSelectedListener onChatRoomSelectedListener) {
        this.onChatRoomSelectedListener = onChatRoomSelectedListener;
        this.chatRoomsAdapter.setOnChatRoomSelectedListener(onChatRoomSelectedListener);
    }

    public void setSelectFirst(boolean selectFirst) {
        this.selectFirst = selectFirst;
    }

    public void selectChatRoom(ChatRoom chatRoom) {
        this.selectableChatRoom = chatRoom;
    }

    private void onUpdatedChatRooms(List<ChatRoomHolder> chatRoomHolders) {
        updateChatRooms(chatRoomHolders);
        selectChatRoomIfNeeded(selectableChatRoom);
        chatsList.postDelayed(this::selectFirstIfNeeded, 4000);
    }

    private void updateChatRoomsWithLastMessage(final ChatRoomHolder chatRoomHolder) {
        chatRoomServices.loadLastChatMessage(chatRoomHolder, new ChatRoomInterface.OnChatLastMessageLoadedListener() {
            @Override
            public void onChatLastMessageLoaded(ChatMessage chatMessage) {
                chatRoomsAdapter.addChatRoom(chatRoomHolder);
            }
            @Override
            public void onChatLastMessageLoadFailed() {}
        });
    }

    private void updateChatRooms(List<ChatRoomHolder> chatRoomHolders) {
        chatRoomsAdapter.getChatRooms().beginBatchedUpdates();
        chatRoomsAdapter.getChatRooms().clear();
        for (ChatRoomHolder chatRoomHolder : chatRoomHolders) {
            chatRoomsAdapter.addChatRoom(chatRoomHolder);
        }
        chatRoomsAdapter.fillSelectableWhenNull();
        chatRoomsAdapter.getChatRooms().endBatchedUpdates();
    }

    private void selectFirstIfNeeded() {
        if(selectFirst) {
            selectFirst = false;
            chatRoomsAdapter.selectFirst();
        }
    }

    private void selectChatRoomIfNeeded(ChatRoom chatRoom) {
        if(chatRoom != null) {
            selectableChatRoom = chatRoom;
            this.chatRoomsAdapter.selectChatRoom(chatRoom);
            selectableChatRoom = null;
        }
    }

    public void notifyItemChanged(int index) {
        chatRoomsAdapter.notifyItemChanged(index);
    }

    public void setSelectable(boolean selectable) {
        chatRoomsAdapter.setSelectable(selectable);
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
            chatRoomsAdapter.addAll(chatRooms);
            chatRoomsAdapter.setOnChatRoomSelectedListener(onChatRoomSelectedListener);
            chatsList.setAdapter(chatRoomsAdapter);
        } catch(Exception exception) {
            Log.e(TAG, "updateRoomsForQuery ", exception);
        }
    }

    @NonNull
    private List<ChatRoomHolder> getChatRoomsListForQuery(String query) {
        List<ChatRoomHolder> chatRooms = new ArrayList<>();

        query = query.toLowerCase();
        for (int i = 0; i < chatRoomsAdapter.getChatRooms().size(); i++) {
            ChatRoomHolder chatRoomHolder = chatRoomsAdapter.getChatRooms().get(i);
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
        List<ChatRoomHolder> chatRoomHolderList = new ArrayList<>();
        for (int i = 0; i < chatRoomsAdapter.getChatRooms().size(); i++) {
            ChatRoomHolder chatRoomHolder = chatRoomsAdapter.getChatRooms().get(i);
            chatRoomHolderList.add(chatRoomHolder);
        }
        return chatRoomHolderList;
    }

    private View.OnClickListener onCreateChatRoomListener = view -> {
        if (UserManager.validateKeyAction(getContext())) {
            Intent newChatIntent = new Intent(getContext(), ChatCreationActivity.class);
            newChatIntent.putParcelableArrayListExtra(ChatCreationActivity.EXTRA_CHAT_ROOMS
                    , (ArrayList<ChatRoomHolder>) getChatRooms());
            startActivityForResult(newChatIntent, MainActivity.REQUEST_CODE_CHAT_CREATION);
        }
    };

}
