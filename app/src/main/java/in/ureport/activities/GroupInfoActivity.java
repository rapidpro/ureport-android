package in.ureport.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import in.ureport.R;
import in.ureport.fragments.GroupInfoFragment;
import in.ureport.listener.InfoGroupChatListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.network.ChatRoomServices;

/**
 * Created by johncordeiro on 21/08/15.
 */
public class GroupInfoActivity extends AppCompatActivity implements InfoGroupChatListener {

    public static final int REQUEST_CODE_CHAT_EDITION = 200;
    public static final int RESULT_REMOVED = 20;

    public static final String EXTRA_CHAT_ROOM = "chatRoom";
    public static final String EXTRA_CHAT_MEMBERS = "chatMembers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_generic);
        setResult(Activity.RESULT_CANCELED);

        if(savedInstanceState == null) {
            GroupChatRoom chatRoom = getIntent().getParcelableExtra(EXTRA_CHAT_ROOM);
            ChatMembers chatMembers = getIntent().getParcelableExtra(EXTRA_CHAT_MEMBERS);

            addGroupInfoFragment(chatRoom, chatMembers);
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
        setResult(Activity.RESULT_OK, data);

        ChatRoom chatRoom = data.getParcelableExtra(ChatCreationActivity.EXTRA_RESULT_CHAT_ROOM);
        ChatMembers chatMembers = data.getParcelableExtra(ChatCreationActivity.EXTRA_RESULT_CHAT_MEMBERS);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if(fragment instanceof GroupInfoFragment) {
            GroupInfoFragment groupInfoFragment = (GroupInfoFragment)fragment;
            groupInfoFragment.updateViewForChatRoom(chatRoom, chatMembers);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    private void addGroupInfoFragment(GroupChatRoom chatRoom, ChatMembers chatMembers) {
        GroupInfoFragment groupInfoFragment = GroupInfoFragment.newInstance(chatRoom, chatMembers);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, groupInfoFragment)
                .commit();
    }

    @Override
    public void onEditGroupChat(ChatRoom chatRoom, ChatMembers members) {
        Intent chatCreationIntent = new Intent(this, ChatCreationActivity.class);
        chatCreationIntent.putExtra(EXTRA_CHAT_ROOM, chatRoom);
        chatCreationIntent.putExtra(EXTRA_CHAT_MEMBERS, members);
        startActivityForResult(chatCreationIntent, REQUEST_CODE_CHAT_EDITION);
    }

    @Override
    public void onChatRoomClose(final ChatRoom chatRoom, final ChatMembers members) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.message_close_group)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChatRoomServices chatRoomServices = new ChatRoomServices();
                        chatRoomServices.closeChatRoom(GroupInfoActivity.this, chatRoom, members);

                        setResult(RESULT_REMOVED);
                        supportFinishAfterTransition();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void onChatRoomLeave(ChatRoom chatRoom) {
        UserManager.leaveFromGroup(this, chatRoom);
    }
}
