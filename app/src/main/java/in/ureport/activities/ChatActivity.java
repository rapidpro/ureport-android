package in.ureport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import in.ureport.R;
import in.ureport.fragments.ChatGroupFragment;
import in.ureport.fragments.ListChatRoomsFragment;
import in.ureport.fragments.InviteContactsFragment;
import in.ureport.models.holders.NavigationItem;
import in.ureport.views.adapters.NavigationAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatActivity extends BaseActivity {

    private static final int PAGE_POSITION_MY_CHATS = 1;
    private static final int PAGE_POSITION_GROUPS = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setupView();
    }

    private void setupView() {
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.addOnPageChangeListener(onPageChangeListener);

        NavigationItem chatGroupItem = new NavigationItem(new ChatGroupFragment(), getString(R.string.chat_groups));
        NavigationItem chatRoomsItem = new NavigationItem(new ListChatRoomsFragment(), getString(R.string.chat_rooms));
        NavigationItem inviteItem = new NavigationItem(new InviteContactsFragment(), getString(R.string.chat_invite));

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(), chatGroupItem, chatRoomsItem, inviteItem);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);

        getTabLayout().setupWithViewPager(pager);

        getMainActionButton().setImageResource(R.drawable.ic_edit_white_24dp);
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
}
