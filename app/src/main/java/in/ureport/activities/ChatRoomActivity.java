package in.ureport.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import in.ureport.R;
import in.ureport.fragments.ChatRoomFragment;
import in.ureport.fragments.GroupInfoFragment;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class ChatRoomActivity extends AppCompatActivity implements ChatRoomFragment.ChatRoomListener {

    public static final String EXTRA_CHAT_ROOM = "chatRoom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic);

        if(savedInstanceState == null) {
            ChatRoom chatRoom = getIntent().getParcelableExtra(EXTRA_CHAT_ROOM);
            ChatRoomFragment chatRoomFragment = ChatRoomFragment.newInstance(chatRoom);
            chatRoomFragment.setChatRoomListener(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, chatRoomFragment)
                    .commit();
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
    public void onChatRoomLeave(ChatRoom chatRoom) {
        leaveGroup();
    }

    private void leaveGroup() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.chat_group_leave)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void onChatRoomInfoView(ChatRoom chatRoom) {
        GroupInfoFragment groupInfoFragment = GroupInfoFragment.newInstance((GroupChatRoom)chatRoom);
        groupInfoFragment.setChatRoomListener(this);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content, groupInfoFragment)
                .addToBackStack(null)
                .commit();
    }
}
