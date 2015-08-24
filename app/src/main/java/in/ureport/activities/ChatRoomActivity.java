package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import in.ureport.R;
import in.ureport.fragments.ChatRoomFragment;
import in.ureport.listener.InfoGroupChatListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatRoomActivity extends AppCompatActivity implements ChatRoomFragment.ChatRoomListener
    , InfoGroupChatListener {

    private static final String TAG = "ChatRoomActivity";

    public static final String EXTRA_CHAT_ROOM = "chatRoom";
    public static final String EXTRA_CHAT_MEMBERS = "chatMembers";

    private static final int REQUEST_CODE_GROUP_INFO = 500;

    private ChatRoomFragment chatRoomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_generic);
        setResult(RESULT_OK, getIntent());

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
                case REQUEST_CODE_GROUP_INFO:
                    updateChatData(data);
            }
        } else if(resultCode == GroupInfoActivity.RESULT_REMOVED) {
            finish();
        }
    }

    private void updateChatData(Intent data) {
        ChatRoom chatRoom = data.getParcelableExtra(ChatCreationActivity.EXTRA_CHAT_ROOM);
        ChatMembers chatMembers = data.getParcelableExtra(ChatCreationActivity.EXTRA_CHAT_MEMBERS);

        updateIntentResult(chatRoom, chatMembers);
        if(chatRoomFragment != null)
            chatRoomFragment.updateChatRoom(chatRoom, chatMembers);
    }

    private void updateIntentResult(ChatRoom chatRoom, ChatMembers chatMembers) {
        Intent intent = getIntent();
        intent.putExtra(EXTRA_CHAT_ROOM, chatRoom);
        intent.putExtra(EXTRA_CHAT_MEMBERS, chatMembers);
        setResult(RESULT_OK, intent);
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
    public void onEditGroupChat(ChatRoom chatRoom, ChatMembers members) {}

    @Override
    public void onChatRoomClose(ChatRoom chatRoom, ChatMembers members) {}

    @Override
    public void onChatRoomLeave(ChatRoom chatRoom) {
        UserManager.leaveFromGroup(this, chatRoom);
    }

    @Override
    public void onChatRoomInfoView(ChatRoom chatRoom, ChatMembers chatMembers) {
        Intent groupInfoIntent = new Intent(this, GroupInfoActivity.class);
        groupInfoIntent.putExtra(GroupInfoActivity.EXTRA_CHAT_ROOM, chatRoom);
        groupInfoIntent.putExtra(GroupInfoActivity.EXTRA_CHAT_MEMBERS, chatMembers);
        startActivityForResult(groupInfoIntent, REQUEST_CODE_GROUP_INFO);
    }
}
