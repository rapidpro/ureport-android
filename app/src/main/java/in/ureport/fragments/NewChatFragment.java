package in.ureport.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.ChatRoomInterface;
import in.ureport.listener.OnCreateGroupListener;
import in.ureport.listener.OnCreateIndividualChatListener;
import in.ureport.managers.SearchManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.User;
import in.ureport.models.holders.ChatRoomHolder;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class NewChatFragment extends Fragment implements OnCreateIndividualChatListener
        , SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String TAG = "NewChatFragment";

    private static final String EXTRA_CHAT_ROOMS = "chatRooms";

    private RecyclerView ureportersList;

    private UserServices userServices;
    private ChatRoomServices chatRoomServices;

    private ChatRoomInterface.OnChatRoomSavedListener onChatRoomSavedListener;
    private OnCreateGroupListener onCreateGroupListener;

    private UreportersAdapter ureportersAdapter;
    private List<ChatRoomHolder> existingChatRooms;

    public static NewChatFragment newInstance(ArrayList<ChatRoomHolder> chatRooms) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_CHAT_ROOMS, chatRooms);

        NewChatFragment fragment = new NewChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_CHAT_ROOMS)) {
            existingChatRooms = getArguments().getParcelableArrayList(EXTRA_CHAT_ROOMS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObject();
        setupView(view);
        loadData();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if(activity instanceof ChatRoomInterface.OnChatRoomSavedListener)
            onChatRoomSavedListener = (ChatRoomInterface.OnChatRoomSavedListener)activity;
        if(activity instanceof OnCreateGroupListener)
            onCreateGroupListener = (OnCreateGroupListener)activity;
    }

    private void setupObject() {
        userServices = new UserServices();
        chatRoomServices = new ChatRoomServices();
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);

        TextView createGroup = (TextView) view.findViewById(R.id.saveGroup);
        setupCreateGroupForModerators(createGroup);

        ureportersList = (RecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setupCreateGroupForModerators(TextView createGroup) {
        if(UserManager.canModerate()) {
            createGroup.setOnClickListener(onCreateGroupClickListener);
        } else {
            createGroup.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        userServices.loadByCountryCode(new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(List<User> users) {
                if (ureportersAdapter != null) {
                    ureportersAdapter.update(users);
                } else {
                    setupAdapter(users);
                }
            }
        });
    }

    private void setupAdapter(List<User> users) {
        ureportersAdapter = new UreportersAdapter(users);
        ureportersAdapter.setOnCreateIndividualChatListener(NewChatFragment.this);
        ureportersList.setAdapter(ureportersAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.label_new_chat);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = new SearchManager(getActivity());
        searchManager.addSearchView(menu, R.string.search_hint_users, this, this);
    }

    private View.OnClickListener onCreateGroupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(onCreateGroupListener != null)
                onCreateGroupListener.onCreateGroup();
        }
    };

    @Override
    public void onCreateIndividualChat(final User user) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.chat_create_individual_message)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLoggedUserAndCheck(user);
                    }
                }).create();
        alertDialog.show();
    }

    private void getLoggedUserAndCheck(final User friend) {
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                User me = dataSnapshot.getValue(User.class);
                checkIfExistsAndSaveChat(me, friend);
            }
        });
    }

    private void checkIfExistsAndSaveChat(final User me, final User friend) {
        if(existingChatRooms != null && containsChatRoom(me, friend)) {
            displayDuplicateAlert();
        } else {
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null
                    , getString(R.string.load_message_wait), true, false);
            chatRoomServices.saveIndividualChatRoom(getActivity(), me, friend, new ChatRoomInterface.OnChatRoomSavedListener() {
                @Override
                public void onChatRoomSaved(ChatRoom chatRoom, ChatMembers chatMembers) {
                    progressDialog.dismiss();
                    onChatRoomSavedListener.onChatRoomSaved(chatRoom, chatMembers);
                }
            });
        }
    }

    private boolean containsChatRoom(User me, User friend) {
        for (ChatRoomHolder existingChatRoom : existingChatRooms) {
            if(existingChatRoom.chatRoom.getType() == ChatRoom.Type.Individual
            && existingChatRoom.members.getUsers().contains(me)
            && existingChatRoom.members.getUsers().contains(friend)) {
                return true;
            }
        }
        return false;
    }

    private void displayDuplicateAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.error_chat_create_individual)
                .setNeutralButton(R.string.confirm_neutral_dialog_button, null)
                .create();
        alertDialog.show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ureportersAdapter.search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ureportersAdapter.search(newText);
        return false;
    }

    @Override
    public boolean onClose() {
        ureportersAdapter.clearSearch();
        return false;
    }
}
