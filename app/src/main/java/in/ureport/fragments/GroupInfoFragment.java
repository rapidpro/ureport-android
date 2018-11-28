package in.ureport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.text.DateFormat;

import in.ureport.R;
import in.ureport.helpers.ImageLoader;
import in.ureport.helpers.ValueEventListenerAdapter;
import in.ureport.listener.InfoGroupChatListener;
import in.ureport.managers.UserManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.ChatRoom;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class GroupInfoFragment extends Fragment {

    private static final String EXTRA_CHAT_ROOM = "chatRoom";
    private static final String EXTRA_CHAT_MEMBERS = "chatMembers";

    private InfoGroupChatListener infoGroupChatListener;

    private GroupChatRoom chatRoom;
    private ChatMembers chatMembers;

    private TextView date;
    private TextView description;
    private TextView title;
    private ImageView picture;

    private UreportersAdapter ureportersAdapter;

    public static GroupInfoFragment newInstance(GroupChatRoom chatRoom, ChatMembers chatMembers) {
        GroupInfoFragment groupInfoFragment = new GroupInfoFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CHAT_ROOM, chatRoom);
        args.putParcelable(EXTRA_CHAT_MEMBERS, chatMembers);
        groupInfoFragment.setArguments(args);

        return groupInfoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(EXTRA_CHAT_ROOM)) {
            chatRoom = getArguments().getParcelable(EXTRA_CHAT_ROOM);
            chatMembers = getArguments().getParcelable(EXTRA_CHAT_MEMBERS);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_group_info, menu);
        setupMenuItemVisibility(menu);
    }

    private void setupMenuItemVisibility(Menu menu) {
        boolean groupModerator = isGroupModerator();

        MenuItem editGroup = menu.findItem(R.id.editGroup);
        editGroup.setVisible(groupModerator);

        MenuItem leaveGroup = menu.findItem(R.id.leaveGroup);
        leaveGroup.setVisible(!groupModerator && isCurrentUserMember());

        MenuItem closeGroup = menu.findItem(R.id.closeGroup);
        closeGroup.setVisible(groupModerator);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.editGroup:
                infoGroupChatListener.onEditGroupChat(chatRoom, chatMembers);
                break;
            case R.id.closeGroup:
                infoGroupChatListener.onChatRoomClose(chatRoom, chatMembers);
                break;
            case R.id.leaveGroup:
                infoGroupChatListener.onChatRoomLeave(chatRoom);
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        loadAdministratorInfo();
        updateViewForChatRoom(chatRoom, chatMembers);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if(activity instanceof InfoGroupChatListener) {
            infoGroupChatListener = (InfoGroupChatListener) activity;
        }
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        RecyclerView ureportersList = (RecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        ureportersAdapter = new UreportersAdapter();
        ureportersList.setAdapter(ureportersAdapter);

        TextView ureportersCount = (TextView) view.findViewById(R.id.ureportersCount);
        ureportersCount.setText(getString(R.string.chat_new_invite_ureporters_count, chatMembers.getUsers().size()));

        picture = (ImageView) view.findViewById(R.id.picture);
        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);
        date = (TextView) view.findViewById(R.id.date);

        Button addUreporter = (Button) view.findViewById(R.id.addUreporter);
        addUreporter.setOnClickListener(onAddUreporterClickListener);
        addUreporter.setVisibility(isGroupModerator() ? View.VISIBLE : View.GONE);
    }

    public void updateViewForChatRoom(ChatRoom chatRoom, ChatMembers chatMembers) {
        if(chatRoom instanceof GroupChatRoom) {
            GroupChatRoom groupChatRoom = (GroupChatRoom)chatRoom;

            this.chatRoom = groupChatRoom;
            this.chatMembers = chatMembers;

            title.setText(groupChatRoom.getTitle());
            description.setText(groupChatRoom.getSubject());

            ImageLoader.loadGroupPictureToImageView(picture, groupChatRoom.getPicture());
            ureportersAdapter.update(chatMembers.getUsers());
        }
    }

    private void loadAdministratorInfo() {
        UserServices userServices = new UserServices();
        userServices.getUser(chatRoom.getAdministrator().getKey(), new ValueEventListenerAdapter() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                super.onDataChange(dataSnapshot);

                if (dataSnapshot.exists()) {
                    User administrator = dataSnapshot.getValue(User.class);
                    DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
                    String creationDate = dateFormatter.format(chatRoom.getCreatedDate());

                    date.setText(getString(R.string.chat_group_info_created_date, administrator.getNickname(), creationDate));
                }
            }
        });
    }

    private View.OnClickListener onAddUreporterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        if(infoGroupChatListener != null)
            infoGroupChatListener.onEditGroupChat(chatRoom, chatMembers);
        }
    };

    private boolean isGroupModerator() {
        return chatRoom.getAdministrator().getKey().equals(UserManager.getUserId())
            || UserManager.canModerate();
    }

    private boolean isCurrentUserMember() {
        User user = new User();
        user.setKey(UserManager.getUserId());

        return chatMembers.getUsers() != null && chatMembers.getUsers().contains(user);
    }
}
