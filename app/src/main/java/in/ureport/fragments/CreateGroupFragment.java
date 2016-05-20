package in.ureport.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.ilhasoft.support.tool.EditTextValidator;
import in.ureport.R;
import in.ureport.helpers.MediaPicker;
import in.ureport.helpers.TransferListenerAdapter;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.ChatRoomInterface;
import in.ureport.helpers.ImageLoader;
import in.ureport.managers.TransferManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class CreateGroupFragment extends Fragment {

    private static final String TAG = "CreateGroupFragment";

    private static final int MIN_SIZE_TITLE = 5;
    public static final int MAX_UREPORTERS_GROUP_COUNT = 20;

    private static final String GROUP_CHAT_FOLDER = "chat_group";

    private static final String EXTRA_CHAT_ROOM = "chatRoom";
    private static final String EXTRA_CHAT_MEMBERS = "chatMembers";

    private EditText title;
    private EditText description;
    private SwitchCompat privateGroup;
    private SwitchCompat mediaAllowed;
    private RecyclerView ureportersList;
    private ImageView addPicture;

    private EditTextValidator validator;

    private UreportersAdapter ureportersAdapter;

    private ChatRoomInterface.OnChatRoomSavedListener onChatRoomSavedListener;
    private Uri pictureUri;

    private boolean editMode = false;

    private GroupChatRoom groupChatRoom;
    private ChatMembers members;

    private ChatRoomServices chatRoomServices;
    private UserServices userServices;

    private ValueEventListener userEventListener;

    public static CreateGroupFragment newInstance(GroupChatRoom chatRoom, ChatMembers members) {
        CreateGroupFragment createGroupFragment = new CreateGroupFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CHAT_ROOM, chatRoom);
        args.putParcelable(EXTRA_CHAT_MEMBERS, members);
        createGroupFragment.setArguments(args);

        return createGroupFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null && args.containsKey(EXTRA_CHAT_ROOM)) {
            editMode = true;
            groupChatRoom = args.getParcelable(EXTRA_CHAT_ROOM);
            members = args.getParcelable(EXTRA_CHAT_MEMBERS);
        }
    }

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

        setupViewWhenEditMode();
        loadUsers();
    }

    private void setupViewWhenEditMode() {
        if(editMode) {
            title.setText(groupChatRoom.getTitle());
            description.setText(groupChatRoom.getSubject());
            privateGroup.setChecked(groupChatRoom.getPrivateAccess() != null && groupChatRoom.getPrivateAccess());
            mediaAllowed.setChecked(groupChatRoom.getMediaAllowed() != null && groupChatRoom.getMediaAllowed());

            if(groupChatRoom.getPicture() != null)
                ImageLoader.loadGroupPictureToImageView(addPicture, groupChatRoom.getPicture());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case MediaPicker.REQUEST_PICK_FROM_GALLERY:
                    saveChoosenPicture(data);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(userEventListener != null) userServices.removeCountryCodeListener(userEventListener);
    }

    private void saveChoosenPicture(Intent data) {
        pictureUri = data.getData();

        if(pictureUri != null)
            addPicture.setImageURI(pictureUri);
    }

    private void loadUsers() {
        userEventListener = userServices.loadByCountryCode(new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(List<User> users) {
                ureportersAdapter = new UreportersAdapter(users);
                if (editMode)
                    ureportersAdapter.setSelectionEnabled(true, MAX_UREPORTERS_GROUP_COUNT, members.getUsers());
                else
                    ureportersAdapter.setSelectionEnabled(true, MAX_UREPORTERS_GROUP_COUNT);
                ureportersList.setAdapter(ureportersAdapter);
            }
        });
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if(activity instanceof ChatRoomInterface.OnChatRoomSavedListener)
            onChatRoomSavedListener = (ChatRoomInterface.OnChatRoomSavedListener)activity;
    }

    private void setupObjects() {
        validator = new EditTextValidator();
        chatRoomServices = new ChatRoomServices();
        userServices = new UserServices();
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);

        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        privateGroup = (SwitchCompat) view.findViewById(R.id.privateGroup);
        mediaAllowed = (SwitchCompat) view.findViewById(R.id.mediaAllowed);

        EditText ureportersSearch = (EditText) view.findViewById(R.id.ureportersSearch);
        ureportersSearch.addTextChangedListener(ureportersSearchTextWatcher);
        ureportersSearch.setOnEditorActionListener(onSearchUreporterActionListener);

        ureportersList = (RecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        addPicture = (ImageView) view.findViewById(R.id.addPicture);
        addPicture.setOnClickListener(onAddPictureClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.label_create_group);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_new_group, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveGroup:
                if(editMode) {
                    editGroup();
                } else {
                    createGroup();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editGroup() {
        if(validateFields()) {
            groupChatRoom.setTitle(title.getText().toString());
            groupChatRoom.setSubject(description.getText().toString());
            groupChatRoom.setPrivateAccess(privateGroup.isChecked());
            groupChatRoom.setMediaAllowed(mediaAllowed.isChecked());

            List<User> selectedUreporters = new ArrayList<>(ureportersAdapter.getSelectedUreporters());

            removeUreportersFromGroup(selectedUreporters);
            addUreportersToGroup(selectedUreporters);

            if(pictureUri != null) {
                uploadPictureAndSaveRoom(groupChatRoom);
            } else {
                saveGroupChatRoom(groupChatRoom);
            }
        }
    }

    private void addUreportersToGroup(List<User> selectedUreporters) {
        if(members.getUsers() != null) {
            List<User> addedUsers = new ArrayList<>();
            for (User selectedUreporter : selectedUreporters) {
                if(!members.getUsers().contains(selectedUreporter)) {
                    addedUsers.add(selectedUreporter);
                    chatRoomServices.addChatMember(getActivity(), selectedUreporter, groupChatRoom.getKey());
                }
            }
            members.getUsers().addAll(addedUsers);
        }
    }

    private void removeUreportersFromGroup(List<User> selectedUreporters) {
        if(members.getUsers() != null) {
            List<User> removedUsers = new ArrayList<>();
            for (User user : members.getUsers()) {
                if(!selectedUreporters.contains(user)) {
                    removedUsers.add(user);
                    chatRoomServices.removeChatMember(getActivity(), user, groupChatRoom.getKey());
                }
            }
            members.getUsers().removeAll(removedUsers);
        }
    }

    private void createGroup() {
        if(validateFields()) {
            final GroupChatRoom groupChatRoom = new GroupChatRoom();
            groupChatRoom.setCreatedDate(new Date());
            groupChatRoom.setTitle(title.getText().toString());
            groupChatRoom.setSubject(description.getText().toString());
            groupChatRoom.setPrivateAccess(privateGroup.isChecked());
            groupChatRoom.setMediaAllowed(mediaAllowed.isChecked());

            if(pictureUri != null) {
                uploadPictureAndSaveRoom(groupChatRoom);
            } else {
                saveGroupChatRoom(groupChatRoom);
            }
        }
    }

    private void uploadPictureAndSaveRoom(final GroupChatRoom groupChatRoom) {
        try {
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.load_message_uploading_image)
                    , true, false);

            LocalMedia localMedia = new LocalMedia(pictureUri);
            localMedia.setType(Media.Type.Picture);

            TransferManager transferManager = new TransferManager(getActivity());
            transferManager.transferMedia(localMedia, GROUP_CHAT_FOLDER, new TransferListenerAdapter(getContext(), localMedia) {
                @Override
                public void onTransferFinished(Media media) {
                    super.onTransferFinished(media);
                    progressDialog.dismiss();
                    groupChatRoom.setPicture(media);
                    saveGroupChatRoom(groupChatRoom);
                }

                @Override
                public void onError(int id, Exception exception) {
                    Log.e(TAG, "onError ", exception);
                    progressDialog.dismiss();
                    showUploadError();
                }
            });
        } catch(Exception exception) {
            exception.printStackTrace();
            showUploadError();
        }
    }

    private void saveGroupChatRoom(final GroupChatRoom groupChatRoom) {
        if(editMode) {
            chatRoomServices.updateGroupChatRoom(groupChatRoom);
            onChatRoomSavedListener.onChatRoomSaved(groupChatRoom, members);
        } else {
            loadUserAndSaveChat(groupChatRoom);
        }
    }

    private void loadUserAndSaveChat(final GroupChatRoom groupChatRoom) {
        userServices.getUser(UserManager.getUserId(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                User user = dataSnapshot.getValue(User.class);

                List<User> members = new ArrayList<>(ureportersAdapter.getSelectedUreporters());
                chatRoomServices.saveGroupChatRoom(getActivity(), user, groupChatRoom, members, onChatRoomSavedListener);
            }
        });
    }

    private void showUploadError() {
        Toast.makeText(getActivity(), R.string.error_image_upload, Toast.LENGTH_SHORT).show();
    }

    private boolean validateFields() {
        boolean titleValid = validator.validateSize(title, MIN_SIZE_TITLE, getString(R.string.error_minimum_size, MIN_SIZE_TITLE));
        boolean descriptionValid = validator.validateEmpty(description, getString(R.string.error_required_field));

        return titleValid && descriptionValid;
    }

    private View.OnClickListener onAddPictureClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MediaPicker mediaPicker = new MediaPicker();
            mediaPicker.pickImageFromGallery(CreateGroupFragment.this);
        }
    };

    private TextWatcher ureportersSearchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            String query = text.toString();
            if(ureportersAdapter != null) {
                ureportersAdapter.search(query);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private TextView.OnEditorActionListener onSearchUreporterActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
            String query = textView.getText().toString();
            ureportersAdapter.search(query);
            return true;
        }
    };
}
