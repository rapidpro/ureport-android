package in.ureport.fragments;

import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import br.com.ilhasoft.support.tool.UnitConverter;
import in.ureport.R;
import in.ureport.managers.UserViewManager;
import in.ureport.models.ChatMessage;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.IndividualChatRoom;
import in.ureport.models.User;
import in.ureport.tasks.GetUserLoggedTask;
import in.ureport.util.SpaceItemDecoration;
import in.ureport.views.adapters.ChatMessagesAdapter;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatRoomFragment extends Fragment {

    private static final String EXTRA_CHAT_ROOM = "chatRoom";

    private TextView name;
    private TextView message;
    private RecyclerView messagesList;

    private ChatMessagesAdapter adapter;

    private ChatRoom chatRoom;
    private User user;

    private ChatRoomListener chatRoomListener;

    private Handler handler = new Handler();

    public static ChatRoomFragment newInstance(ChatRoom chatRoom) {
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CHAT_ROOM, chatRoom);
        chatRoomFragment.setArguments(args);

        return chatRoomFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_CHAT_ROOM)) {
            chatRoom = getArguments().getParcelable(EXTRA_CHAT_ROOM);
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
        setupView(view);
        loadLocalUser();
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        handler.removeCallbacks(friendResponseRunnable);
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

        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        spaceItemDecoration.setVerticalSpaceHeight((int) new UnitConverter(getActivity()).convertDpToPx(10));
        messagesList.addItemDecoration(spaceItemDecoration);

        ImageButton send = (ImageButton) view.findViewById(R.id.send);
        send.setOnClickListener(onSendClickListener);
    }

    private void setupViewForGroupChat(View view) {
        GroupChatRoom groupChatRoom = (GroupChatRoom)chatRoom;
        name.setText(groupChatRoom.getChatGroup().getTitle());

        View info = view.findViewById(R.id.info);
        info.setOnClickListener(onInfoClickListener);
    }

    private void setupViewForIndividualChat(View view) {
        IndividualChatRoom individualChatRoom = (IndividualChatRoom)chatRoom;
        name.setText("@" + individualChatRoom.getFriend().getUsername());

        ImageView picture = (ImageView) view.findViewById(R.id.picture);
        picture.setImageResource(UserViewManager.getUserImage(getActivity(), individualChatRoom.getFriend()));
        picture.setVisibility(View.VISIBLE);
    }

    private void loadLocalUser() {
        new GetUserLoggedTask(getActivity()) {
            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                ChatRoomFragment.this.user = user;

                adapter = new ChatMessagesAdapter(new ArrayList<ChatMessage>(), user);
                messagesList.setAdapter(adapter);
            }
        }.execute();
    }

    private void generateFriendResponseDelayed() {
        handler.postDelayed(friendResponseRunnable, 2000);
    }

    private User getFriend() {
        if(chatRoom instanceof GroupChatRoom) {
            GroupChatRoom groupChatRoom = ((GroupChatRoom)chatRoom);
            return groupChatRoom.getParticipants().get(getRandomInt(0, groupChatRoom.getParticipants().size()-1));
        } else {
            IndividualChatRoom individualChatRoom = ((IndividualChatRoom)chatRoom);
            return individualChatRoom.getFriend();
        }
    }

    private void addChatMessage(ChatMessage chatMessage) {
        adapter.addChatMessage(chatMessage);
        messagesList.scrollToPosition(0);
    }

    private int getRandomInt(int min, int max){
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
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
                chatMessage.setChatRoom(chatRoom);

                addChatMessage(chatMessage);
                generateFriendResponseDelayed();
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

    private Runnable friendResponseRunnable = new Runnable() {
        @Override
        public void run() {
            String[] responses = getResources().getStringArray(R.array.responses);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setDate(new Date());
            chatMessage.setMessage(responses[getRandomInt(0, responses.length - 1)]);
            chatMessage.setUser(getFriend());
            chatMessage.setChatRoom(chatRoom);

            addChatMessage(chatMessage);
        }
    };

    public interface ChatRoomListener {
        void onChatRoomLeave(ChatRoom chatRoom);
        void onChatRoomInfoView(ChatRoom chatRoom);
    }
}
