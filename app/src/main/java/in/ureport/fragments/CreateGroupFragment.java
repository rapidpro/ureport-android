package in.ureport.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import br.com.ilhasoft.support.tool.EditTextValidator;
import in.ureport.R;
import in.ureport.listener.OnChatRoomCreatedListener;
import in.ureport.loader.UreportersLoader;
import in.ureport.managers.PrototypeManager;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class CreateGroupFragment extends Fragment {

    private static final int MIN_SIZE_TITLE = 5;
    private static final int MAX_UREPORTERS_SELECTION_COUNT = 20;

    private EditText title;
    private EditText description;
    private SwitchCompat privateGroup;
    private SwitchCompat mediaAllowed;
    private RecyclerView ureportersList;

    private EditTextValidator validator;

    private UreportersAdapter ureportersAdapter;

    private OnChatRoomCreatedListener onChatRoomCreatedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
        loadUsers();
    }

    private void loadUsers() {
        UserServices userServices = new UserServices();
        userServices.loadAll(new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(List<User> users) {
                ureportersAdapter = new UreportersAdapter(users);
                ureportersAdapter.setSelectionEnabled(true, MAX_UREPORTERS_SELECTION_COUNT);
                ureportersList.setAdapter(ureportersAdapter);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnChatRoomCreatedListener)
            onChatRoomCreatedListener = (OnChatRoomCreatedListener)activity;
    }

    private void setupObjects() {
        validator = new EditTextValidator();
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);

        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        privateGroup = (SwitchCompat) view.findViewById(R.id.privateGroup);
        mediaAllowed = (SwitchCompat) view.findViewById(R.id.mediaAllowed);

        ureportersList = (RecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton addPicture = (FloatingActionButton) view.findViewById(R.id.addPicture);
        addPicture.setOnClickListener(onAddPictureClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.label_create_group);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_new_group, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createGroup:
                createGroup();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createGroup() {
        if(validateFields()) {
            GroupChatRoom groupChatRoom = new GroupChatRoom();
            groupChatRoom.setCreationDate(new Date());
            groupChatRoom.setTitle(title.getText().toString());
            groupChatRoom.setDescription(description.getText().toString());
            groupChatRoom.setPrivateAccess(privateGroup.isChecked());
            groupChatRoom.setMediaAllowed(mediaAllowed.isChecked());

            List<User> members = new ArrayList<>(ureportersAdapter.getSelectedUreporters());

            ChatRoomServices chatRoomServices = new ChatRoomServices();
            chatRoomServices.saveGroupChatRoom(groupChatRoom, members, onChatRoomCreatedListener);
        }
    }

    private boolean validateFields() {
        boolean titleValid = validator.validateSize(title, MIN_SIZE_TITLE, getString(R.string.error_minimum_size, MIN_SIZE_TITLE));
        boolean descriptionValid = validator.validateEmpty(description, getString(R.string.error_required_field));

        return titleValid && descriptionValid;
    }

    private View.OnClickListener onAddPictureClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PrototypeManager.showPrototypeAlert(getActivity());
        }
    };
}
