package in.ureport.fragments;

import android.os.Bundle;
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

import java.text.DateFormat;

import br.com.ilhasoft.support.tool.ResourceUtil;
import in.ureport.R;
import in.ureport.managers.PrototypeManager;
import in.ureport.models.ChatMembers;
import in.ureport.models.GroupChatRoom;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 7/21/15.
 */
public class GroupInfoFragment extends Fragment {

    private static final String EXTRA_CHAT_ROOM = "chatRoom";
    private static final String EXTRA_CHAT_MEMBERS = "chatMembers";

    private ChatRoomFragment.ChatRoomListener chatRoomListener;

    private GroupChatRoom chatRoom;
    private ChatMembers chatMembers;

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.editGroup:
                PrototypeManager.showPrototypeAlert(getActivity());
                break;
            case R.id.leaveGroup:
                leaveGroup();
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
    }

    private void setupView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        RecyclerView ureportersList = (RecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));

        UreportersAdapter ureportersAdapter = new UreportersAdapter(chatMembers.getUsers());
        ureportersList.setAdapter(ureportersAdapter);

        TextView ureportersCount = (TextView) view.findViewById(R.id.ureportersCount);
        ureportersCount.setText(getString(R.string.chat_new_invite_ureporters_count, chatMembers.getUsers().size()));

        ResourceUtil resourceUtil = new ResourceUtil(getActivity());

        ImageView picture = (ImageView) view.findViewById(R.id.picture);
        picture.setImageResource(resourceUtil.getDrawableId(chatRoom.getPicture(), R.drawable.face));

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(chatRoom.getTitle());

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(chatRoom.getDescription());

        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
        String creationDate = dateFormatter.format(chatRoom.getCreationDate());

        TextView date = (TextView) view.findViewById(R.id.date);
        date.setText(getString(R.string.chat_group_info_created_date, creationDate));

        Button addUreporter = (Button) view.findViewById(R.id.addUreporter);
        addUreporter.setOnClickListener(onAddUreporterClickListener);
    }

    public void setChatRoomListener(ChatRoomFragment.ChatRoomListener chatRoomListener) {
        this.chatRoomListener = chatRoomListener;
    }

    private void leaveGroup() {
        if(chatRoomListener != null)
            chatRoomListener.onChatRoomLeave(chatRoom);
    }

    private View.OnClickListener onAddUreporterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PrototypeManager.showPrototypeAlert(getActivity());
        }
    };
}
