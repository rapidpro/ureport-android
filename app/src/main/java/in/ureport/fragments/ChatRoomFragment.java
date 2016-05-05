package in.ureport.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.helpers.MediaSelector;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.ChatRoomInterface;
import in.ureport.listener.InfoGroupChatListener;
import in.ureport.helpers.ImageLoader;
import in.ureport.managers.MediaViewer;
import in.ureport.managers.TransferManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.LocalMedia;
import in.ureport.models.Media;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.helpers.SpaceItemDecoration;
import in.ureport.network.UserServices;
import in.ureport.tasks.CleanUnreadByRoomTask;
import in.ureport.tasks.SendGcmChatTask;
import in.ureport.views.adapters.ChatMessagesAdapter;
import in.ureport.views.holders.ChatMessageViewHolder;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatRoomFragment extends Fragment
        implements ChatMessageViewHolder.OnChatMessageSelectedListener, PickMediaFragment.OnPickMediaListener {

    private static final String TAG = "ChatRoomFragment";

    private static final String EXTRA_CHAT_ROOM = "chatRoom";
    private static final String EXTRA_CHAT_MEMBERS = "chatMembers";
    private static final String EXTRA_CHAT_ROOM_KEY = "chatRoomKey";
    private static final String EXTRA_STANDALONE_MODE = "standaloneMode";

    private static final String MEDIA_PARENT = "chat_message";
    public static final int REMOVE_CHAT_MESSAGE_POSITION = 0;

    private TextView name;
    private TextView message;
    private ImageView picture;
    private View info;
    private RecyclerView messagesList;
    private ImageButton send;
    private ImageView record;
    private ImageView attachFile;

    private ChatMessagesAdapter adapter;

    private ChatRoom chatRoom;
    private ChatMembers chatMembers;
    private String chatRoomKey;
    private boolean standaloneMode = false;

    private User user;

    private ChatRoomListener chatRoomListener;
    private InfoGroupChatListener infoGroupChatListener;

    private ChatRoomServices chatRoomServices;
    private UserServices userServices;

    private PickMediaFragment pickMediaFragment;
    private MediaViewer mediaViewer;

    public static ChatRoomFragment newInstance(ChatRoom chatRoom, ChatMembers chatMembers, boolean standaloneMode) {
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CHAT_ROOM, chatRoom);
        args.putParcelable(EXTRA_CHAT_MEMBERS, chatMembers);
        args.putBoolean(EXTRA_STANDALONE_MODE, standaloneMode);
        chatRoomFragment.setArguments(args);

        return chatRoomFragment;
    }

    public static ChatRoomFragment newInstance(String chatRoomKey) {
        Bundle args = new Bundle();
        args.putString(EXTRA_CHAT_ROOM_KEY, chatRoomKey);

        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            if(getArguments().containsKey(EXTRA_CHAT_ROOM_KEY)) {
                chatRoomKey = getArguments().getString(EXTRA_CHAT_ROOM_KEY);
            } else {
                chatRoom = getArguments().getParcelable(EXTRA_CHAT_ROOM);
                chatMembers = getArguments().getParcelable(EXTRA_CHAT_MEMBERS);
                standaloneMode = getArguments().getBoolean(EXTRA_STANDALONE_MODE, false);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObjects();
        setupView(view);
        setupData();
    }

    private void setupData() {
        if(chatRoom != null) {
            loadData();
            updateViewForChatRoom();
        } else {
            chatRoomServices.getChatRoom(chatRoomKey, onLoadChatRoomByKeyListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendMedia(LocalMedia media) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null
                , getString(R.string.load_message_uploading_image), true, true);

        try {
            TransferManager transferManager = new TransferManager(getActivity());

            progressDialog.setOnCancelListener((DialogInterface dialog) -> {
                transferManager.cancelTransfer();
                Toast.makeText(getContext(), R.string.message_upload_cancel, Toast.LENGTH_SHORT).show();
            });

            transferManager.transferMedias(Collections.singletonList(media), MEDIA_PARENT, new TransferManager.OnTransferMediasListener() {
                @Override
                public void onTransferMedias(Map<LocalMedia, Media> medias) {
                    progressDialog.dismiss();
                    sendChatMessageWithMedia(medias.values().iterator().next());
                }

                @Override
                public void onWaitingConnection() {
                    progressDialog.setMessage(getString(R.string.load_message_waiting_connection));
                }

                @Override
                public void onFailed() {
                    progressDialog.dismiss();
                    displayMessage(R.string.error_media_upload);
                }
            });
        } catch(Exception exception) {
            Log.e(TAG, "sendMedia ", exception);
            displayMessage(R.string.error_take_picture);
        }
    }

    private void sendChatMessageWithMedia(Media media) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUser(user);
        chatMessage.setDate(new Date());
        chatMessage.setMedia(media);
        saveChatMessage(chatMessage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CleanUnreadByRoomTask cleanUnreadByRoomTask = new CleanUnreadByRoomTask(getActivity());
        cleanUnreadByRoomTask.execute(chatRoom);

        if(chatRoom != null) {
            chatRoomServices.removeEventListenerForChatMessages(chatRoom.getKey(), onChildEventListener);
            chatRoomServices.removeValueListenForChatRoom(chatRoom, onChatRoomChangedListener);
        }
    }

    private void loadData() {
        user = getMemberUserByKey(UserManager.getUserId());
        adapter.setUser(user);

        CleanUnreadByRoomTask cleanUnreadByRoomTask = new CleanUnreadByRoomTask(getActivity());
        cleanUnreadByRoomTask.execute(chatRoom);

        chatRoomServices.addChildEventListenerForChatMessages(chatRoom.getKey(), onChildEventListener);
        chatRoomServices.addValueListenForChatRoom(chatRoom, onChatRoomChangedListener);
    }

    private void setupObjects() {
        userServices = new UserServices();
        chatRoomServices = new ChatRoomServices();
        mediaViewer = new MediaViewer((AppCompatActivity) getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(chatRoom != null
        && ((getParentFragment() != null && getParentFragment().isMenuVisible()) || getParentFragment() == null)) {
            switch(chatRoom.getType()) {
                case Group:
                    inflater.inflate(R.menu.menu_chat_group, menu);
                    setupMenuItemVisibilityGroupChat(menu);
                    break;
                case Individual:
                    inflater.inflate(R.menu.menu_chat_individual, menu);
                    setupMenuItemVisibilityIndividualChat(menu);
            }
        }
    }

    private void setupMenuItemVisibilityGroupChat(Menu menu) {
        MenuItem leaveGroupItem = menu.findItem(R.id.leaveGroup);
        leaveGroupItem.setVisible(!isCurrentUserAdministrator());
    }

    private void setupMenuItemVisibilityIndividualChat(Menu menu) {
        IndividualChatRoom individualChatRoom = (IndividualChatRoom) chatRoom;

        MenuItem blockChatRoomItem = menu.findItem(R.id.blockChatRoom);
        MenuItem unblockChatRoomItem = menu.findItem(R.id.unblockChatRoom);

        if(individualChatRoom.getBlocked() != null) {
            if(individualChatRoom.getBlocked().equals(UserManager.getUserId())) {
                attachFile.setEnabled(false);
                blockChatRoomItem.setVisible(false);
                unblockChatRoomItem.setVisible(true);
            } else {
                attachFile.setEnabled(true);
                blockChatRoomItem.setVisible(false);
                unblockChatRoomItem.setVisible(false);
            }
        } else {
            attachFile.setEnabled(true);
            blockChatRoomItem.setVisible(true);
            unblockChatRoomItem.setVisible(false);
        }
    }

    private boolean isCurrentUserAdministrator() {
        GroupChatRoom groupChatRoom = (GroupChatRoom)chatRoom;
        return groupChatRoom.getAdministrator().getKey().equals(UserManager.getUserId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.leaveGroup:
                if (chatRoomListener != null)
                    infoGroupChatListener.onChatRoomLeave(chatRoom);
                return true;
            case R.id.groupInfo:
                if (chatRoomListener != null) {
                    chatRoomListener.onChatRoomInfoView(chatRoom, chatMembers, getPairs());
                }
                return true;
            case R.id.blockChatRoom:
                displayAlert(R.string.message_confirm_block_user, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        displayMessage(R.string.message_block_chat_room);
                        chatRoomServices.blockChatRoom(chatRoom);
                    }
                });
                return true;
            case R.id.unblockChatRoom:
                displayAlert(R.string.message_confirm_unblock_user, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        displayMessage(R.string.message_unblock_chat_room);
                        chatRoomServices.unblockChatRoom(chatRoom);
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickMedia() {
        pickMediaFragment = new PickMediaFragment();
        pickMediaFragment.setOnPickMediaListener(this);
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom, R.anim.slide_in_top, R.anim.slide_out_bottom)
                .add(R.id.content, pickMediaFragment)
                .commit();
    }

    @NonNull
    private Pair<View, String>[] getPairs() {
        Pair<View, String> picturePair = Pair.create((View)picture, getString(R.string.transition_profile_picture));
        Pair<View, String> nicknamePair = Pair.create((View)name, getString(R.string.transition_profile_nickname));

        Pair<View, String> [] pairs = (Pair<View, String> []) Array.newInstance(Pair.class, 2);
        pairs[0] = picturePair;
        pairs[1] = nicknamePair;
        return pairs;
    }

    private void displayAlert(@StringRes int messageId, DialogInterface.OnClickListener confirmListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(messageId)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, confirmListener).create();
        alertDialog.show();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if(activity instanceof ChatRoomListener) {
            chatRoomListener = (ChatRoomListener) activity;
        }

        if(activity instanceof InfoGroupChatListener) {
            infoGroupChatListener = (InfoGroupChatListener) activity;
        }
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if(!standaloneMode) {
            AppCompatActivity activity = ((AppCompatActivity) getActivity());
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            toolbar.setVisibility(View.GONE);
        }
        setHasOptionsMenu(true);

        name = (TextView) view.findViewById(R.id.name);
        message = (TextView) view.findViewById(R.id.message);
        message.addTextChangedListener(onMessageTextWatcher);
        info = view.findViewById(R.id.info);

        picture = (ImageView) view.findViewById(R.id.picture);

        messagesList = (RecyclerView) view.findViewById(R.id.messagesList);
        messagesList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));

        adapter = new ChatMessagesAdapter(user);
        adapter.setOnChatMessageSelectedListener(this);
        messagesList.setAdapter(adapter);

        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        spaceItemDecoration.setVerticalSpaceHeight((int) new UnitConverter(getActivity()).convertDpToPx(10));
        messagesList.addItemDecoration(spaceItemDecoration);

        attachFile = (ImageView) view.findViewById(R.id.attachFile);
        attachFile.setOnClickListener(v -> pickMedia());

        send = (ImageButton) view.findViewById(R.id.send);
        send.setOnClickListener(onSendClickListener);

        record = (ImageView) view.findViewById(R.id.record);
        record.setOnClickListener(v -> {
            MediaSelector mediaSelector = new MediaSelector(getContext());
            mediaSelector.pickAudio(ChatRoomFragment.this, onPickAudioListener);
        });

        if(pickMediaFragment != null)
            pickMediaFragment.setOnPickMediaListener(this);
    }

    public void updateChatRoom(ChatRoom chatRoom, ChatMembers chatMembers) {
        this.chatRoom = chatRoom;
        this.chatMembers = chatMembers;

        updateViewForChatRoom();
    }

    private void updateViewForChatRoom() {
        if(chatRoom instanceof IndividualChatRoom) {
            setupViewForIndividualChat();
        } else if(chatRoom instanceof GroupChatRoom) {
            setupViewForGroupChat();
        }
    }

    private User getMemberUserByKey(String key) {
        if(chatMembers == null || chatMembers.getUsers() == null) return null;

        User memberUser = new User();
        memberUser.setKey(key);

        int authUserIndex = chatMembers.getUsers().indexOf(memberUser);
        return authUserIndex >= 0 ? chatMembers.getUsers().get(authUserIndex) : null;
    }

    private void setupViewForGroupChat() {
        GroupChatRoom groupChatRoom = (GroupChatRoom)chatRoom;
        name.setText(groupChatRoom.getTitle());

        info.setOnClickListener(onInfoClickListener);
        ImageLoader.loadGroupPictureToImageView(picture, groupChatRoom.getPicture());
    }

    private void setupViewForIndividualChat() {
        User friend = getFriend(chatMembers);
        if(friend != null) {
            name.setText(friend.getNickname());
            ImageLoader.loadPersonPictureToImageView(picture, friend.getPicture());
        }
    }

    private void refreshChatRoomBlocking(ChatRoom chatRoom) {
        if(chatRoom.getType() != ChatRoom.Type.Individual) return;
        IndividualChatRoom individualChatRoom = (IndividualChatRoom) chatRoom;
        getActivity().supportInvalidateOptionsMenu();

        if(individualChatRoom.getBlocked() != null) {
            User blockerUser = new User();
            blockerUser.setKey(individualChatRoom.getBlocked());

            int indexOfBlockerUser = chatMembers.getUsers().indexOf(blockerUser);
            if (indexOfBlockerUser >= 0) {
                blockerUser = chatMembers.getUsers().get(indexOfBlockerUser);
                message.setEnabled(false);
                message.setText(getString(R.string.message_individual_chat_blocked, blockerUser.getNickname()));
                send.setEnabled(false);
                showAlertIfNeeded(individualChatRoom, blockerUser);
            }
        } else {
            message.setEnabled(true);
            message.setText(null);
            send.setEnabled(true);
        }
    }

    private void showAlertIfNeeded(IndividualChatRoom individualChatRoom, User blockerUser) {
        if(!individualChatRoom.getBlocked().equals(UserManager.getUserId())) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.message_user_blocked_by_other, blockerUser.getNickname()))
                    .setNeutralButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            getActivity().finish();
                        }
                    }).create();
            alertDialog.show();
        }
    }

    private User getFriend(ChatMembers members) {
        for (User user : members.getUsers()) {
            if(!user.equals(this.user)) {
                return user;
            }
        }
        return null;
    }

    private void addChatMessage(ChatMessage chatMessage) {
        adapter.addChatMessage(chatMessage);
        messagesList.scrollToPosition(0);
    }

    private View.OnClickListener onSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String messageText = message.getText().toString();
            if(messageText.length() > 0) {
                message.setText("");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setDate(new Date());
                chatMessage.setMessage(messageText);
                chatMessage.setUser(user);

                saveChatMessage(chatMessage);
            }
        }
    };

    private void saveChatMessage(ChatMessage chatMessage) {
        SendGcmChatTask sendGcmChatTask = new SendGcmChatTask(getActivity(), chatRoom, chatMessage.getUser());
        sendGcmChatTask.execute(chatMessage);

        chatRoomServices.saveChatMessage(chatRoom, chatMessage);
    }

    private View.OnClickListener onInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (chatRoomListener != null)
                chatRoomListener.onChatRoomInfoView(chatRoom, chatMembers, getPairs());
        }
    };

    private ValueEventListenerAdapter onChatRoomChangedListener = new ValueEventListenerAdapter() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            super.onDataChange(dataSnapshot);
            if(dataSnapshot.exists()) {
                chatRoom = chatRoomServices.getChatRoomFromSnapshot(dataSnapshot);
                refreshChatRoomBlocking(chatRoom);
            }
        }
    };

    private ChildEventListenerAdapter onChildEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
            super.onChildAdded(dataSnapshot, previousChild);
            addChatMessage(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            super.onChildRemoved(dataSnapshot);
            removeChatMessage(dataSnapshot);
        }
    };

    private void removeChatMessage(DataSnapshot dataSnapshot) {
        ChatMessage chatMessage = getChatMessageBySnapshot(dataSnapshot);
        adapter.removeChatMessage(chatMessage);
    }

    private void addChatMessage(DataSnapshot dataSnapshot) {
        try {
            ChatMessage message = getChatMessageBySnapshot(dataSnapshot);

            User memberUser = getMemberUserByKey(message.getUser().getKey());
            if (memberUser != null) {
                message.setUser(memberUser);
                addChatMessage(message);
            } else {
                loadUserAndAddChatMessage(message);
            }
        } catch(Exception exception) {
            Log.e(TAG, "onChildAdded ", exception);
        }
    }

    @NonNull
    private ChatMessage getChatMessageBySnapshot(DataSnapshot dataSnapshot) {
        final ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
        message.setKey(dataSnapshot.getKey());
        return message;
    }

    private void loadUserAndAddChatMessage(final ChatMessage message) {
        userServices.getUser(message.getUser().getKey(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);
                User user = dataSnapshot.getValue(User.class);
                chatMembers.getUsers().add(user);

                message.setUser(user);
                addChatMessage(message);
            }
        });
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    private ChatRoomInterface.OnChatRoomLoadedListener onLoadChatRoomByKeyListener = new ChatRoomInterface.OnChatRoomLoadedListener() {
        @Override
        public void onChatRoomLoadFailed() {}

        @Override
        public void onChatRoomLoaded(ChatRoom chatRoom, ChatMembers chatMembers) {
            ChatRoomFragment.this.chatRoom = chatRoom;
            ChatRoomFragment.this.chatMembers = chatMembers;

            loadData();
            updateViewForChatRoom();
        }
    };

    @Override
    public void onMediaChatMessageView(ChatMessage chatMessage, ImageView mediaImageView) {
        mediaViewer.viewMedia(chatMessage.getMedia(), mediaImageView);
    }

    @Override
    public void onChatMessageSelected(final ChatMessage chatMessage) {
        if(chatMessage.getUser().getKey().equals(UserManager.getUserId()) || UserManager.canModerate()) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setItems(R.array.chat_message_items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which) {
                                case REMOVE_CHAT_MESSAGE_POSITION:
                                    removeChatMessageWithAlert(chatMessage);
                            }
                        }
                    }).create();
            alertDialog.show();
        }
    }

    private void removeChatMessageWithAlert(final ChatMessage chatMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.message_remove_chat_message)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chatRoomServices.removeChatMessage(chatRoom, chatMessage, onChatMessageRemovedListener);
                    }
                })
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .create();
        alertDialog.show();
    }

    private Firebase.CompletionListener onChatMessageRemovedListener = new Firebase.CompletionListener() {
        @Override
        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            if(firebaseError == null) {
                displayMessage(R.string.message_delete_message_success);
            } else {
                displayMessage(R.string.error_remove);
            }
        }
    };

    private void displayMessage(int messageId) {
        Toast.makeText(getActivity(), messageId, Toast.LENGTH_SHORT).show();
    }

    private void dismissMediaPicker() {
        if(pickMediaFragment != null)
            pickMediaFragment.dismiss();
    }

    @Override
    public void onPickMedia(Media media) {
        if(media instanceof LocalMedia) {
            sendMedia((LocalMedia) media);
        } else {
            sendChatMessageWithMedia(media);
        }
        dismissMediaPicker();
    }

    private TextWatcher onMessageTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {
            if (sequence != null && sequence.length() > 0) {
                send.setVisibility(View.VISIBLE);
                record.setVisibility(View.GONE);
            } else {
                record.setVisibility(View.VISIBLE);
                send.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable watcher) {}
    };

    private MediaSelector.OnLoadLocalMediaListener onPickAudioListener = new MediaSelector.OnLoadLocalMediaListener() {
        @Override
        public void onLoadLocalImage(Uri uri) {}
        @Override
        public void onLoadLocalVideo(Uri uri) {}
        @Override
        public void onLoadFile(Uri uri) {}
        @Override
        public void onLoadAudio(Uri uri, int duration) {
            HashMap<String, Object> metadata = new HashMap<>();
            metadata.put(Media.KEY_DURATION, duration);

            LocalMedia media = new LocalMedia();
            media.setType(Media.Type.Audio);
            media.setPath(uri);
            media.setMetadata(metadata);
            sendMedia(media);
        }
    };

    public void setChatRoomListener(ChatRoomListener chatRoomListener) {
        this.chatRoomListener = chatRoomListener;
    }

    public void setInfoGroupChatListener(InfoGroupChatListener infoGroupChatListener) {
        this.infoGroupChatListener = infoGroupChatListener;
    }

    public interface ChatRoomListener {
        void onChatRoomInfoView(ChatRoom chatRoom, ChatMembers chatMembers, Pair<View, String>... pairs);
    }
}
