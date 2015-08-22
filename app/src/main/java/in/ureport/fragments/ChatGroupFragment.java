package in.ureport.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.helpers.DividerItemDecoration;
import in.ureport.managers.SearchManager;
import in.ureport.models.GroupChatRoom;
import in.ureport.network.ChatRoomServices;
import in.ureport.views.adapters.ChatGroupAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatGroupFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String TAG = "ChatGroupFragment";

    private RecyclerView groupsList;

    private ChatGroupAdapter adapter;

    private ChatRoomServices chatRoomServices;

    private ChatGroupAdapter.ChatGroupListener chatGroupListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        setupObjects();
        setupView(view);
        addEventListenerForGroupChats();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        SearchManager searchManager = new SearchManager(getActivity());
        searchManager.addSearchView(menu, R.string.hint_search_groups, this, this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && groupsList != null) groupsList.setAdapter(adapter);
    }

    private void setupObjects() {
        chatRoomServices = new ChatRoomServices();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatRoomServices.removeChildEventListenerForPublicGroups(onChildEventForGroupChatListener);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ChatGroupAdapter.ChatGroupListener) {
            chatGroupListener = (ChatGroupAdapter.ChatGroupListener) activity;
        }
    }

    private void addEventListenerForGroupChats() {
        chatRoomServices.addChildEventListenerForPublicGroups(onChildEventForGroupChatListener);
    }

    private void setupView(View view) {
        groupsList = (RecyclerView) view.findViewById(R.id.groupsList);
        groupsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupsList.addItemDecoration(new DividerItemDecoration(getActivity()));

        adapter = new ChatGroupAdapter();
        adapter.setChatGroupListener(chatGroupListener);
        groupsList.setAdapter(adapter);
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

    @Override
    public boolean onClose() {
        groupsList.setAdapter(adapter);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        createAdapterWithQuery(query);
        return false;
    }

    private void createAdapterWithQuery(String query) {
        try {
            List<GroupChatRoom> groupChatRoomsSearch = new ArrayList<>();
            List<GroupChatRoom> groupChatRooms = adapter.getGroupChats();

            query = query.toLowerCase();
            for (GroupChatRoom groupChatRoom : groupChatRooms) {
                if(groupChatRoom.getTitle().toLowerCase().contains(query)
                || (groupChatRoom.getDescription() != null && groupChatRoom.getDescription().toLowerCase().contains(query))) {
                    groupChatRoomsSearch.add(groupChatRoom);
                }
            }

            ChatGroupAdapter chatGroupAdapter = new ChatGroupAdapter();
            chatGroupAdapter.updateData(groupChatRoomsSearch);
            chatGroupAdapter.setChatGroupListener(chatGroupListener);
            groupsList.setAdapter(chatGroupAdapter);
        } catch(Exception exception) {
            Log.e(TAG, "updateRoomsForQuery ", exception);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
