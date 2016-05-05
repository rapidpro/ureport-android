package in.ureport.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.view.View;
import android.widget.ImageView;

import in.ureport.R;
import in.ureport.fragments.ChatRoomFragment;
import in.ureport.fragments.MediaFragment;
import in.ureport.fragments.MediaViewFragment;
import in.ureport.listener.InfoGroupChatListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.Media;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatRoomActivity extends AppCompatActivity implements ChatRoomFragment.ChatRoomListener
    , InfoGroupChatListener, MediaViewFragment.OnCloseMediaViewListener {

    public static final String EXTRA_CHAT_ROOM_KEY = "chatRoomKey";
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
            initializeChatRoomFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, chatRoomFragment)
                    .commit();
        }
    }

    private void initializeChatRoomFragment() {
        if(getIntent().hasExtra(EXTRA_CHAT_ROOM_KEY)) {
            String chatRoomKey = getIntent().getStringExtra(EXTRA_CHAT_ROOM_KEY);
            chatRoomFragment = ChatRoomFragment.newInstance(chatRoomKey);
        } else {
            ChatRoom chatRoom = getIntent().getParcelableExtra(EXTRA_CHAT_ROOM);
            ChatMembers chatMembers = getIntent().getParcelableExtra(EXTRA_CHAT_MEMBERS);
            chatRoomFragment = ChatRoomFragment.newInstance(chatRoom, chatMembers, false);
        }
        addSharedElementTranstion(chatRoomFragment);
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
            supportFinishAfterTransition();
        }
    }

    private void updateChatData(Intent data) {
        ChatRoom chatRoom = data.getParcelableExtra(ChatCreationActivity.EXTRA_RESULT_CHAT_ROOM);
        ChatMembers chatMembers = data.getParcelableExtra(ChatCreationActivity.EXTRA_RESULT_CHAT_MEMBERS);

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
            supportFinishAfterTransition();
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


    private void addSharedElementTranstion(Fragment fragment) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ChangeBounds changeBoundsTransition = new ChangeBounds();
            changeBoundsTransition.setDuration(300);
            fragment.setSharedElementEnterTransition(changeBoundsTransition);
        }
    }

    @Override
    public void onCloseMediaView() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onChatRoomInfoView(ChatRoom chatRoom, ChatMembers chatMembers, Pair<View, String>... pairs) {
        Intent groupInfoIntent = new Intent(this, GroupInfoActivity.class);
        groupInfoIntent.putExtra(GroupInfoActivity.EXTRA_CHAT_ROOM, chatRoom);
        groupInfoIntent.putExtra(GroupInfoActivity.EXTRA_CHAT_MEMBERS, chatMembers);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
        ActivityCompat.startActivityForResult(this, groupInfoIntent, REQUEST_CODE_GROUP_INFO, options.toBundle());
    }
}
