package in.ureport.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;

import in.ureport.R;
import in.ureport.activities.ChatRoomActivity;
import in.ureport.listener.OnChatRoomLoadedListener;
import in.ureport.managers.FirebaseManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.holders.ChatRoomHolder;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.util.ChildEventListenerAdapter;
import in.ureport.util.DividerItemDecoration;
import in.ureport.views.adapters.ChatRoomsAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ListChatRoomsFragment extends Fragment implements ChatRoomsAdapter.OnChatRoomSelectedListener {

    private static final String TAG = "ListChatRoomsFragment";

    private ChatRoomServices chatRoomServices;
    private ChatRoomsAdapter chatRoomsAdapter;

    private UserServices userServices;

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
        userServices.addChildEventListenerForChatRooms(FirebaseManager.getAuthUserKey(), childEventListener);
    }

    private ChildEventListenerAdapter childEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
            super.onChildAdded(dataSnapshot, previousChild);

            String chatRoomKey = dataSnapshot.getKey();

            chatRoomServices.getChatRoom(chatRoomKey, new OnChatRoomLoadedListener() {
                @Override
                public void onChatRoomLoaded(ChatRoom chatRoom, ChatMembers chatMembers, ChatMessage lastMessage) {
                    chatRoomsAdapter.addChatRoom(new ChatRoomHolder(chatRoom, chatMembers, lastMessage));
                }
            });
        }
    };

    @Override
    public void onChatRoomSelected(ChatRoom chatRoom, ChatMembers members) {
        Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM, chatRoom);
        chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_MEMBERS, members);
        startActivity(chatRoomIntent);
    }
}
