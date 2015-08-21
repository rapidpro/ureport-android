package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;

import in.ureport.R;
import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.helpers.DividerItemDecoration;
import in.ureport.listener.OnChatMembersLoadedListener;
import in.ureport.models.ChatMembers;
import in.ureport.models.GroupChatRoom;
import in.ureport.network.ChatRoomServices;
import in.ureport.views.adapters.ChatGroupAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatGroupFragment extends Fragment implements ChatGroupAdapter.ChatGroupListener {

    private RecyclerView groupsList;

    private ChatGroupAdapter adapter;

    private ChatRoomServices chatRoomServices;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObjects();
        setupView(view);
        addEventListenerForGroupChats();
    }

    private void setupObjects() {
        chatRoomServices = new ChatRoomServices();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatRoomServices.removeEventListener(onChildEventForGroupChatListener);
    }

    private void addEventListenerForGroupChats() {
        chatRoomServices.addChildEventListenerForPublicGroups(onChildEventForGroupChatListener);
    }

    private void setupView(View view) {
        groupsList = (RecyclerView) view.findViewById(R.id.groupsList);
        groupsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupsList.addItemDecoration(new DividerItemDecoration(getActivity()));

        adapter = new ChatGroupAdapter();
        adapter.setChatGroupListener(this);
        groupsList.setAdapter(adapter);
    }

    @Override
    public void onJoinChatGroup(GroupChatRoom groupChatRoom) {

    }

    @Override
    public void onViewGroupInfo(final GroupChatRoom groupChatRoom) {
        ChatRoomServices chatRoomServices = new ChatRoomServices();
        chatRoomServices.loadChatRoomMembers(groupChatRoom.getKey(), new OnChatMembersLoadedListener() {
            @Override
            public void onChatMembersLoaded(ChatMembers chatMembers) {
                addGroupInfoFragment(chatMembers, groupChatRoom);
            }
        });
    }

    private void addGroupInfoFragment(ChatMembers chatMembers, GroupChatRoom groupChatRoom) {
        GroupInfoFragment groupInfoFragment = GroupInfoFragment.newInstance(groupChatRoom, chatMembers);
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, groupInfoFragment)
                .addToBackStack(null)
                .commit();
    }

    private ChildEventListenerAdapter onChildEventForGroupChatListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
            super.onChildAdded(dataSnapshot, previousChild);

            GroupChatRoom groupChatRoom = dataSnapshot.getValue(GroupChatRoom.class);
            groupChatRoom.setKey(dataSnapshot.getKey());

            adapter.addGroupChatRoom(groupChatRoom);
        }
    };
}
