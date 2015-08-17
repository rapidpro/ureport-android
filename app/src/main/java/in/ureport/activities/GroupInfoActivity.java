package in.ureport.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import in.ureport.R;
import in.ureport.fragments.GroupInfoFragment;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.ChatGroup;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/24/15.
 */
public class GroupInfoActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT_GROUP = "chatGroup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountryProgramManager.setThemeIfNeeded(this);
        setContentView(R.layout.activity_generic);

        if(savedInstanceState == null) {
            ChatGroup chatGroup = getIntent().getParcelableExtra(EXTRA_CHAT_GROUP);

            GroupChatRoom groupChatRoom = new GroupChatRoom();


            GroupInfoFragment groupInfoFragment = GroupInfoFragment.newInstance(groupChatRoom);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, groupInfoFragment)
                    .commit();
        }
    }
}
