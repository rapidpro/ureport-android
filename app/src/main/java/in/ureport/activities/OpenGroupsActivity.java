package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import in.ureport.R;
import in.ureport.fragments.ChatGroupFragment;
import in.ureport.fragments.CreateGroupFragment;
import in.ureport.listener.ChatRoomInterface;
import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;
import in.ureport.network.ChatRoomServices;
import in.ureport.views.adapters.ChatGroupAdapter;

/**
 * Created by johncordeiro on 11/09/15.
 */
public class OpenGroupsActivity extends AppCompatActivity implements ChatGroupAdapter.ChatGroupListener {

    private ChatRoomServices chatRoomServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_open_groups);
        setupToolbar();
        setupObjects();

        if(savedInstanceState == null) {
            ChatGroupFragment chatGroupFragment = new ChatGroupFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, chatGroupFragment)
                    .commit();
        }
    }

    private void setupObjects() {
        chatRoomServices = new ChatRoomServices();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    @Override
    public void onJoinChatGroup(final GroupChatRoom groupChatRoom) {
        chatRoomServices.loadChatRoomMembers(groupChatRoom.getKey(), new ChatRoomInterface.OnChatMembersLoadedListener() {
            @Override
            public void onChatMembersLoaded(ChatMembers chatMembers) {
                joinChatGroup(chatMembers, groupChatRoom);
            }
        });
    }

    private void joinChatGroup(ChatMembers chatMembers, GroupChatRoom groupChatRoom) {
        if(UserManager.validateKeyAction(this)) {
            User me = new User();
            me.setKey(UserManager.getUserId());

            if(chatMembers.getUsers().contains(me)) {
                showMessage(R.string.error_already_join_group);
            } else if(chatMembers.getUsers().size() < CreateGroupFragment.MAX_UREPORTERS_GROUP_COUNT) {
                chatRoomServices.addChatMember(this, me, groupChatRoom.getKey());
                showMessage(R.string.success_message_join_group);
            } else {
                showMessage(R.string.error_full_group);
            }
        }
    }

    private void showMessage(@StringRes int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewGroupInfo(final GroupChatRoom groupChatRoom) {
        ChatRoomServices chatRoomServices = new ChatRoomServices();
        chatRoomServices.loadChatRoomMembers(groupChatRoom.getKey(), new ChatRoomInterface.OnChatMembersLoadedListener() {
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
