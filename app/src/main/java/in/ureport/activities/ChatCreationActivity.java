package in.ureport.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import in.ureport.R;
import in.ureport.fragments.CreateGroupFragment;
import in.ureport.fragments.NewChatFragment;
import in.ureport.listener.ChatRoomInterface;
import in.ureport.listener.OnCreateGroupListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;
import in.ureport.models.holders.ChatRoomHolder;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatCreationActivity extends AppCompatActivity implements ChatRoomInterface.OnChatRoomSavedListener
    , OnCreateGroupListener {

    public static final String EXTRA_RESULT_CHAT_ROOM = "chatRoom";
    public static final String EXTRA_RESULT_CHAT_MEMBERS = "chatMembers";

    public static final String EXTRA_CHAT_ROOMS = "chatRooms";
    public static final String EXTRA_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_new_chat);
        setupView();
        setResult(Activity.RESULT_CANCELED);

        if(savedInstanceState == null) {
            if(getIntent().hasExtra(EXTRA_RESULT_CHAT_ROOM)) {
                ChatRoom chatRoom = getIntent().getParcelableExtra(EXTRA_RESULT_CHAT_ROOM);
                ChatMembers chatMembers = getIntent().getParcelableExtra(EXTRA_RESULT_CHAT_MEMBERS);

                addCreateGroupFragment(chatRoom, chatMembers);
            } else {
                ArrayList<ChatRoomHolder> chatRooms = getIntent().getParcelableArrayListExtra(EXTRA_CHAT_ROOMS);
                User user = getIntent().getParcelableExtra(EXTRA_USER);
                addNewChatFragment(chatRooms, user);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }

        return true;
    }

    private void addNewChatFragment(ArrayList<ChatRoomHolder> chatRooms, User user) {
        NewChatFragment newChatFragment;
        if(user != null) {
            newChatFragment = NewChatFragment.newInstance(chatRooms, user);
        } else {
            newChatFragment = NewChatFragment.newInstance(chatRooms);
        }
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, newChatFragment)
                .commit();
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onChatRoomSaved(ChatRoom chatRoom, ChatMembers chatMembers) {
        Intent data = new Intent();
        data.putExtra(EXTRA_RESULT_CHAT_ROOM, chatRoom);
        data.putExtra(EXTRA_RESULT_CHAT_MEMBERS, chatMembers);

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onCreateGroup() {
        addCreateGroupFragment();
    }

    private void addCreateGroupFragment() {
        CreateGroupFragment createGroupFragment = new CreateGroupFragment();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, createGroupFragment)
                .addToBackStack(null)
                .commit();
    }

    private void addCreateGroupFragment(ChatRoom chatRoom, ChatMembers chatMembers) {
        CreateGroupFragment createGroupFragment = CreateGroupFragment.newInstance((GroupChatRoom)chatRoom, chatMembers);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content, createGroupFragment)
                .commit();
    }
}
