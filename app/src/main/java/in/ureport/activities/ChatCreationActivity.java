package in.ureport.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import in.ureport.R;
import in.ureport.fragments.CreateGroupFragment;
import in.ureport.fragments.NewChatFragment;
import in.ureport.listener.ChatCreationListener;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatCreationActivity extends AppCompatActivity implements ChatCreationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        setupView();

        if(savedInstanceState == null) {
            addNewChatFragment();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            finish();

        return true;
    }

    private void addNewChatFragment() {
        NewChatFragment newChatFragment = new NewChatFragment();
        newChatFragment.setChatCreationListener(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, newChatFragment)
                .commit();
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateGroupChatCalled() {
        CreateGroupFragment createGroupFragment = new CreateGroupFragment();
        createGroupFragment.setChatCreationListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, createGroupFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreateIndividualChatCalled() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.chat_create_individual_message)
                .setNegativeButton(R.string.cancel_dialog_button, null)
                .setPositiveButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onChatRoomCreated();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void onChatRoomCreated() {
        finish();
    }
}
