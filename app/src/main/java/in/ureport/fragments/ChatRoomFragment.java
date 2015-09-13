package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.firebase.client.DataSnapshot;

import java.util.Date;

import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.InfoGroupChatListener;
import in.ureport.helpers.ImageLoader;
import in.ureport.listener.OnChatRoomLoadedListener;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.helpers.ChildEventListenerAdapter;
import in.ureport.helpers.SpaceItemDecoration;
import in.ureport.network.UserServices;
import in.ureport.tasks.CleanUnreadByRoomTask;
import in.ureport.tasks.SendGcmChatTask;
import in.ureport.views.adapters.ChatMessagesAdapter;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatRoomFragment extends Fragment {

    private static final String TAG = "ChatRoomFragment";

    private static final String EXTRA_CHAT_ROOM = "chatRoom";
    private static final String EXTRA_CHAT_MEMBERS = "chatMembers";
    private static final String EXTRA_CHAT_ROOM_KEY = "chatRoomKey";

    private TextView name;
    private TextView message;
    private ImageView picture;
    private View info;
    private RecyclerView messagesList;

    private ChatMessagesAdapter adapter;

    private ChatRoom chatRoom;
    private ChatMembers chatMembers;
    private String chatRoomKey;

    private User user;

    private ChatRoomListener chatRoomListener;
    private InfoGroupChatListener infoGroupChatListener;

    private ChatRoomServices chatRoomServices;
    private UserServices userServices;

    public static ChatRoomFragment newInstance(ChatRoom chatRoom, ChatMembers chatMembers) {
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CHAT_ROOM, chatRoom);
        args.putParcelable(EXTRA_CHAT_MEMBERS, chatMembers);
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
    public void onDestroyView() {
        super.onDestroyView();
        CleanUnreadByRoomTask cleanUnreadByRoomTask = new CleanUnreadByRoomTask();
        cleanUnreadByRoomTask.execute(chatRoom);

        chatRoomServices.removeEventListenerForChatMessages(chatRoom.getKey(), onChildEventListener);
    }

    private void loadData() {
        user = getMemberUserByKey(UserManager.getUserId());
        adapter.setUser(user);

        CleanUnreadByRoomTask cleanUnreadByRoomTask = new CleanUnreadByRoomTask();
        cleanUnreadByRoomTask.execute(chatRoom);

        chatRoomServices.addChildEventListenerForChatMessages(chatRoom.getKey(), onChildEventListener);
    }

    private void setupObjects() {
        userServices = new UserServices();
        chatRoomServices = new ChatRoomServices();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(chatRoom != null && chatRoom.getType() == ChatRoom.Type.Group) {
            inflater.inflate(R.menu.menu_chat_group, menu);
            setupMenuItemVisibility(menu);
        }
    }

    private void setupMenuItemVisibility(Menu menu) {
        MenuItem leaveGroupItem = menu.findItem(R.id.leaveGroup);
        leaveGroupItem.setVisible(!isCurrentUserAdministrator());
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
                if (chatRoomListener != null)
                    chatRoomListener.onChatRoomInfoView(chatRoom, chatMembers);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        name = (TextView) view.findViewById(R.id.name);
        message = (TextView) view.findViewById(R.id.message);
        info = view.findViewById(R.id.info);

        picture = (ImageView) view.findViewById(R.id.picture);

        messagesList = (RecyclerView) view.findViewById(R.id.messagesList);
        messagesList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));

        adapter = new ChatMessagesAdapter(user);
        messagesList.setAdapter(adapter);

        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        spaceItemDecoration.setVerticalSpaceHeight((int) new UnitConverter(getActivity()).convertDpToPx(10));
        messagesList.addItemDecoration(spaceItemDecoration);

        ImageButton send = (ImageButton) view.findViewById(R.id.send);
        send.setOnClickListener(onSendClickListener);
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
        name.setText(friend.getNickname());

        ImageLoader.loadPersonPictureToImageView(picture, friend.getPicture());
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

                Log.i(TAG, "onClick user: " + user);

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
                chatRoomListener.onChatRoomInfoView(chatRoom, chatMembers);
        }
    };

    private ChildEventListenerAdapter onChildEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
            super.onChildAdded(dataSnapshot, previousChild);
            addChatMessage(dataSnapshot);
        }
    };

    private void addChatMessage(DataSnapshot dataSnapshot) {
        try {
            final ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
            message.setKey(dataSnapshot.getKey());

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

    private OnChatRoomLoadedListener onLoadChatRoomByKeyListener = new OnChatRoomLoadedListener() {
        @Override
        public void onChatRoomLoaded(ChatRoom chatRoom, ChatMembers chatMembers, ChatMessage lastMessage) {
            ChatRoomFragment.this.chatRoom = chatRoom;
            ChatRoomFragment.this.chatMembers = chatMembers;

            loadData();
            updateViewForChatRoom();
        }
    };

    public interface ChatRoomListener {
        void onChatRoomInfoView(ChatRoom chatRoom, ChatMembers chatMembers);
    }
}
