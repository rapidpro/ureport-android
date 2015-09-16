package in.ureport.fragments;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;

import java.util.List;

import in.ureport.R;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.OnCreateGroupListener;
import in.ureport.listener.OnCreateIndividualChatListener;
import in.ureport.listener.OnChatRoomSavedListener;
import in.ureport.managers.SearchManager;
import in.ureport.managers.UserManager;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class NewChatFragment extends Fragment implements OnCreateIndividualChatListener
        , SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String TAG = "NewChatFragment";

    private RecyclerView ureportersList;

    private UserServices userServices;
    private ChatRoomServices chatRoomServices;

    private OnChatRoomSavedListener onChatRoomSavedListener;
    private OnCreateGroupListener onCreateGroupListener;

    private UreportersAdapter adapter;

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
        if(activity instanceof OnChatRoomSavedListener)
            onChatRoomSavedListener = (OnChatRoomSavedListener)activity;
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
        userServices.loadAll(new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(List<User> users) {
                if(adapter != null) {
                    adapter.update(users);
                } else {
                    setupAdapter(users);
                }
            }
        });
    }

    private void setupAdapter(List<User> users) {
        adapter = new UreportersAdapter(users);
        adapter.setOnCreateIndividualChatListener(NewChatFragment.this);
        ureportersList.setAdapter(adapter);
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
                        getLoggedUserAndSaveChat(user);
                    }
                }).create();
        alertDialog.show();
    }

    private void getLoggedUserAndSaveChat(final User friend) {
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);

                User me = dataSnapshot.getValue(User.class);
                chatRoomServices.saveIndividualChatRoom(getActivity(), me, friend, onChatRoomSavedListener);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        userServices.loadByName(query, new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(List<User> users) {
                if(adapter != null)
                    adapter.update(users);
            }
        });
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        loadData();
        return false;
    }
}
