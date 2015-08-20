package in.ureport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import in.ureport.R;
import in.ureport.fragments.CreateGroupFragment;
import in.ureport.fragments.NewChatFragment;
import in.ureport.listener.OnChatRoomCreatedListener;
import in.ureport.listener.OnCreateGroupListener;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.ChatRoom;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatCreationActivity extends AppCompatActivity implements OnChatRoomCreatedListener
    , OnCreateGroupListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
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
    public void onChatRoomCreated(ChatRoom chatRoom) {
        finish();
    }

    @Override
    public void onCreateGroup() {
        addCreateGroupFragment();
    }

    private void addCreateGroupFragment() {
        CreateGroupFragment createGroupFragment = new CreateGroupFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, createGroupFragment)
                .addToBackStack(null)
                .commit();
    }
}
