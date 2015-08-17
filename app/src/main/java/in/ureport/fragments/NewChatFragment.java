package in.ureport.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.listener.ChatCreationListener;
import in.ureport.listener.OnChatRoomCreatedListener;
import in.ureport.loader.UreportersLoader;
import in.ureport.managers.FirebaseManager;
import in.ureport.models.ChatRoom;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class NewChatFragment extends Fragment implements ChatCreationListener {

    private RecyclerView ureportersList;

    private UserServices userServices;
    private ChatRoomServices chatRoomServices;

    private OnChatRoomCreatedListener onChatRoomCreatedListener;

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

    private void setupObject() {
        userServices = new UserServices();
        chatRoomServices = new ChatRoomServices();
    }

    private void setupView(View view) {
        TextView createGroup = (TextView) view.findViewById(R.id.createGroup);
        createGroup.setOnClickListener(onCreateGroupClickListener);

        ureportersList = (RecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadData();
    }

    private void loadData() {
        userServices.loadAll(new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(List<User> users) {
                UreportersAdapter adapter = new UreportersAdapter(users);
                adapter.setChatCreationListener(NewChatFragment.this);
                ureportersList.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.label_new_chat);
    }

    private View.OnClickListener onCreateGroupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onCreateGroupChatCalled() {
    }

    @Override
    public void onCreateIndividualChatCalled(final User user) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.chat_create_individual_message)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chatRoomServices.saveIndividualChatRoom(user, onChatRoomCreatedListener);
                    }
                }).create();
        alertDialog.show();
    }

    public void setOnChatRoomCreatedListener(OnChatRoomCreatedListener onChatRoomCreatedListener) {
        this.onChatRoomCreatedListener = onChatRoomCreatedListener;
    }
}
