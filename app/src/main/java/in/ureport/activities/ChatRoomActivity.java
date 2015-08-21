package in.ureport.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.ChatRoomFragment;
import in.ureport.fragments.GroupInfoFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatRoomActivity extends AppCompatActivity implements ChatRoomFragment.ChatRoomListener {

    public static final String EXTRA_CHAT_ROOM = "chatRoom";
    public static final String EXTRA_CHAT_MEMBERS = "chatMembers";

    private static final int REQUEST_CODE_CHAT_EDITION = 200;

    private ChatRoomFragment chatRoomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_generic);

        if(savedInstanceState == null) {
            ChatRoom chatRoom = getIntent().getParcelableExtra(EXTRA_CHAT_ROOM);
            ChatMembers chatMembers = getIntent().getParcelableExtra(EXTRA_CHAT_MEMBERS);

            chatRoomFragment = ChatRoomFragment.newInstance(chatRoom, chatMembers);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, chatRoomFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHAT_EDITION:
                    updateChatData(data);
            }
        }
    }

    private void updateChatData(Intent data) {
        ChatRoom chatRoom = data.getParcelableExtra(ChatCreationActivity.EXTRA_CHAT_ROOM);
        ChatMembers chatMembers = data.getParcelableExtra(ChatCreationActivity.EXTRA_CHAT_MEMBERS);

        if(chatRoomFragment != null)
            chatRoomFragment.updateChatRoom(chatRoom, chatMembers);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if(fragment instanceof GroupInfoFragment) {
            GroupInfoFragment groupInfoFragment = (GroupInfoFragment)fragment;
            groupInfoFragment.updateViewForChatRoom(chatRoom, chatMembers);
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

    @Override
    public void onEditGroupChat(ChatRoom chatRoom, ChatMembers members) {
        Intent chatCreationIntent = new Intent(this, ChatCreationActivity.class);
        chatCreationIntent.putExtra(EXTRA_CHAT_ROOM, chatRoom);
        chatCreationIntent.putExtra(EXTRA_CHAT_MEMBERS, members);
        startActivityForResult(chatCreationIntent, REQUEST_CODE_CHAT_EDITION);
    }

    @Override
    public void onChatRoomLeave(ChatRoom chatRoom) {
        leaveGroup(chatRoom);
    }

    private void leaveGroup(final ChatRoom chatRoom) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.chat_group_leave)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        User user = new User();
                        user.setKey(FirebaseManager.getAuthUserKey());

                        ChatRoomServices chatRoomServices = new ChatRoomServices();
                        chatRoomServices.removeChatMember(user, chatRoom.getKey());

                        finish();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void onChatRoomInfoView(ChatRoom chatRoom, ChatMembers chatMembers) {
        GroupInfoFragment groupInfoFragment = GroupInfoFragment.newInstance((GroupChatRoom)chatRoom, chatMembers);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, groupInfoFragment)
                .addToBackStack(null)
                .commit();
    }
}
