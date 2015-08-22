package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import in.ureport.R;
import in.ureport.fragments.ChatGroupFragment;
import in.ureport.fragments.CreateGroupFragment;
import in.ureport.fragments.ListChatRoomsFragment;
import in.ureport.fragments.InviteContactsFragment;
import in.ureport.listener.OnChatMembersLoadedListener;
import in.ureport.managers.FirebaseManager;
import in.ureport.models.ChatMembers;
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

    private ChatRoomServices chatRoomServices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setupObjects();
        setupView();
    }

    private void setupObjects() {
        chatRoomServices = new ChatRoomServices();
    }

    private void setupView() {
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.addOnPageChangeListener(onPageChangeListener);

        NavigationItem chatGroupItem = new NavigationItem(new ChatGroupFragment(), getString(R.string.chat_groups));
        NavigationItem chatRoomsItem = new NavigationItem(new ListChatRoomsFragment(), getString(R.string.chat_rooms));
        NavigationItem inviteItem = new NavigationItem(new InviteContactsFragment(), getString(R.string.chat_invite));

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(), chatGroupItem, chatRoomsItem, inviteItem);
        pager.setAdapter(adapter);
        getTabLayout().setupWithViewPager(pager);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(1);

        getMainActionButton().setImageResource(R.drawable.ic_add_white_24dp);
        getMainActionButton().setOnClickListener(onCreateChatClickListener);
        getMenuNavigation().getMenu().findItem(R.id.chat).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean hasTabLayout() {
        return true;
    }

    @Override
    public boolean hasMainActionButton() {
        return true;
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageScrollStateChanged(int state) {}
        @Override
        public void onPageSelected(int position) {
            if(position == PAGE_POSITION_GROUPS || position == PAGE_POSITION_MY_CHATS) {
                getMainActionButton().setVisibility(View.VISIBLE);
            } else {
                getMainActionButton().setVisibility(View.GONE);
            }
        }
    };

    private View.OnClickListener onCreateChatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent newChatIntent = new Intent(ChatActivity.this, ChatCreationActivity.class);
            startActivity(newChatIntent);
        }
    };

    @Override
    public void onJoinChatGroup(final GroupChatRoom groupChatRoom) {
        chatRoomServices.loadChatRoomMembers(groupChatRoom.getKey(), new OnChatMembersLoadedListener() {
            @Override
            public void onChatMembersLoaded(ChatMembers chatMembers) {
                joinChatGroup(chatMembers, groupChatRoom);
            }
        });
    }

    private void joinChatGroup(ChatMembers chatMembers, GroupChatRoom groupChatRoom) {
        User me = new User();
        me.setKey(FirebaseManager.getAuthUserKey());

        if(chatMembers.getUsers().contains(me)) {
            showMessage(R.string.error_already_join_group);
        } else if(chatMembers.getUsers().size() < CreateGroupFragment.MAX_UREPORTERS_GROUP_COUNT) {
            chatRoomServices.addChatMember(me, groupChatRoom.getKey());
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
        chatRoomServices.loadChatRoomMembers(groupChatRoom.getKey(), new OnChatMembersLoadedListener() {
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
