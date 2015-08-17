package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.ImageLoader;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.util.ChildEventListenerAdapter;
import in.ureport.util.SpaceItemDecoration;
import in.ureport.views.adapters.ChatMessagesAdapter;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatRoomFragment extends Fragment {

    private static final String EXTRA_CHAT_ROOM = "chatRoom";
    private static final String EXTRA_CHAT_MEMBERS = "chatMembers";

    private TextView name;
    private TextView message;
    private RecyclerView messagesList;

    private ChatMessagesAdapter adapter;

    private ChatRoom chatRoom;
    private ChatMembers chatMembers;
    private User user;

    private ChatRoomListener chatRoomListener;
    private ChatRoomServices chatRoomServices;

    public static ChatRoomFragment newInstance(ChatRoom chatRoom, ChatMembers chatMembers) {
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CHAT_ROOM, chatRoom);
        args.putParcelable(EXTRA_CHAT_MEMBERS, chatMembers);
        chatRoomFragment.setArguments(args);

        return chatRoomFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_CHAT_ROOM)) {
            chatRoom = getArguments().getParcelable(EXTRA_CHAT_ROOM);
            chatMembers = getArguments().getParcelable(EXTRA_CHAT_MEMBERS);
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

        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseManager.getReference().removeEventListener(onChildEventListener);
    }

    private void loadData() {
        chatRoomServices.addChildEventListenerForChatMessages(chatRoom.getKey(), onChildEventListener);
    }

    private void setupObjects() {
        chatRoomServices = new ChatRoomServices();
        user = getMemberUserByKey(FirebaseManager.getAuthUserKey());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(chatRoom != null && chatRoom instanceof GroupChatRoom)
            inflater.inflate(R.menu.menu_chat_group, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.leaveGroup:
                if (chatRoomListener != null)
                    chatRoomListener.onChatRoomLeave(chatRoom);
                return true;
            case R.id.groupInfo:
                if (chatRoomListener != null)
                    chatRoomListener.onChatRoomInfoView(chatRoom);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        name = (TextView) view.findViewById(R.id.name);
        message = (TextView) view.findViewById(R.id.message);

        if(chatRoom instanceof IndividualChatRoom) {
            setupViewForIndividualChat(view);
        } else if(chatRoom instanceof GroupChatRoom) {
            setupViewForGroupChat(view);
        }

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

    private User getMemberUserByKey(String key) {
        User memberUser = new User();
        memberUser.setKey(key);

        int authUserIndex = chatMembers.getUsers().indexOf(memberUser);
        return chatMembers.getUsers().get(authUserIndex);
    }

    private void setupViewForGroupChat(View view) {
        GroupChatRoom groupChatRoom = (GroupChatRoom)chatRoom;
        name.setText(groupChatRoom.getTitle());

        View info = view.findViewById(R.id.info);
        info.setOnClickListener(onInfoClickListener);
    }

    private void setupViewForIndividualChat(View view) {
        User friend = getFriend(chatMembers);
        name.setText(friend.getNickname());

        ImageView picture = (ImageView) view.findViewById(R.id.picture);
        ImageLoader.loadPersonPictureToImageView(picture, friend.getPicture());
        picture.setVisibility(View.VISIBLE);
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

    public void setChatRoomListener(ChatRoomListener chatRoomListener) {
        this.chatRoomListener = chatRoomListener;
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

                chatRoomServices.saveChatMessage(chatRoom, chatMessage);
            }
        }
    };

    private View.OnClickListener onInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (chatRoomListener != null)
                chatRoomListener.onChatRoomInfoView(chatRoom);
        }
    };

    private ChildEventListenerAdapter onChildEventListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
            super.onChildAdded(dataSnapshot, previousChild);

            ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
            message.setUser(getMemberUserByKey(message.getUser().getKey()));
            message.setKey(dataSnapshot.getKey());

            addChatMessage(message);
        }
    };

    public interface ChatRoomListener {
        void onChatRoomLeave(ChatRoom chatRoom);
        void onChatRoomInfoView(ChatRoom chatRoom);
    }
}
