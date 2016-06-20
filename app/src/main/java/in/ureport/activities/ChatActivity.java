package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import in.ureport.R;
import in.ureport.fragments.ChatGroupFragment;
import in.ureport.fragments.CreateGroupFragment;
import in.ureport.fragments.ListChatRoomsFragment;
import in.ureport.fragments.InviteContactsFragment;
import in.ureport.listener.ChatRoomInterface;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;
import in.ureport.models.holders.NavigationItem;
import in.ureport.network.ChatRoomServices;
import in.ureport.views.adapters.ChatGroupAdapter;
import in.ureport.views.adapters.NavigationAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatActivity extends BaseActivity implements ChatGroupAdapter.ChatGroupListener {

    private static final int PAGE_POSITION_MY_CHATS = 1;
    private static final int PAGE_POSITION_GROUPS = 0;

    public static final int REQUEST_CODE_CHAT_CREATION = 200;

    private ChatRoomServices chatRoomServices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setupObjects();
        setupView();
        checkUserLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHAT_CREATION:
                    startChatRoom(data);
            }
        }
    }

    private void startChatRoom(Intent data) {
        ChatRoom chatRoom = data.getParcelableExtra(ChatCreationActivity.EXTRA_RESULT_CHAT_ROOM);
        ChatMembers chatMembers = data.getParcelableExtra(ChatCreationActivity.EXTRA_RESULT_CHAT_MEMBERS);

        if(chatRoom != null && chatMembers != null) {
            Intent chatRoomIntent = new Intent(this, ChatRoomActivity.class);
            chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM, chatRoom);
            chatRoomIntent.putExtra(ChatRoomActivity.EXTRA_CHAT_MEMBERS, chatMembers);
            startActivity(chatRoomIntent);
        }
    }

    private void checkUserLogin() {
        if(!UserManager.isUserLoggedIn()) {
            UserManager.startLoginFlow(this);
            finish();
        }
    }

    private void setupObjects() {
        chatRoomServices = new ChatRoomServices();
    }

    private void setupView() {
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        NavigationItem chatGroupItem = new NavigationItem(new ChatGroupFragment(), getString(R.string.label_chat_groups));
        NavigationItem chatRoomsItem = new NavigationItem(new ListChatRoomsFragment(), getString(R.string.chat_rooms));
        NavigationItem inviteItem = new NavigationItem(new InviteContactsFragment(), getString(R.string.chat_invite));

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(), chatGroupItem, chatRoomsItem, inviteItem);
        pager.setAdapter(adapter);
        getTabLayout().setupWithViewPager(pager);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(1);
    }

    @Override
    public boolean hasTabLayout() {
        return true;
    }

    @Override
    public boolean hasMainActionButton() {
        return true;
    }

    private View.OnClickListener onCreateChatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent newChatIntent = new Intent(ChatActivity.this, ChatCreationActivity.class);
            startActivityForResult(newChatIntent, REQUEST_CODE_CHAT_CREATION);
        }
    };

    @Override
    public void onJoinChatGroup(final GroupChatRoom groupChatRoom) {
        chatRoomServices.loadChatRoomMembersWithData(groupChatRoom.getKey(), new ChatRoomInterface.OnChatMembersLoadedListener() {
            @Override
            public void onChatMembersLoaded(ChatMembers chatMembers) {
                joinChatGroup(chatMembers, groupChatRoom);
            }
        });
    }

    private void joinChatGroup(ChatMembers chatMembers, GroupChatRoom groupChatRoom) {
        User me = new User();
        me.setKey(UserManager.getUserId());
        me.setCountryProgram(UserManager.getCountryCode());

        if(chatMembers.getUsers().contains(me)) {
            showMessage(R.string.error_already_join_group);
        } else if(chatMembers.getUsers().size() < CreateGroupFragment.MAX_UREPORTERS_GROUP_COUNT) {
            chatRoomServices.addChatMember(this, me, groupChatRoom.getKey());
            showMessage(R.string.success_message_join_group);
        } else {
            showMessage(R.string.error_full_group);
        }
    }

    private void showMessage(@StringRes int message) {
        Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewGroupInfo(final GroupChatRoom groupChatRoom) {
        ChatRoomServices chatRoomServices = new ChatRoomServices();
        chatRoomServices.loadChatRoomMembersWithData(groupChatRoom.getKey(), new ChatRoomInterface.OnChatMembersLoadedListener() {
            @Override
            public void onChatMembersLoaded(ChatMembers chatMembers) {
                startGroupInfoActivity(groupChatRoom, chatMembers);
            }
        });
    }

    private void startGroupInfoActivity(GroupChatRoom groupChatRoom, ChatMembers chatMembers) {
        Intent groupInfoIntent = new Intent(this, GroupInfoActivity.class);
        groupInfoIntent.putExtra(GroupInfoActivity.EXTRA_CHAT_ROOM, groupChatRoom);
        groupInfoIntent.putExtra(GroupInfoActivity.EXTRA_CHAT_MEMBERS, chatMembers);
        startActivity(groupInfoIntent);
    }
}
