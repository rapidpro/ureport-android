package in.ureport.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.activities.GroupInfoActivity;
import in.ureport.loader.ChatGroupsLoader;
import in.ureport.models.ChatGroup;
import in.ureport.models.GroupChatRoom;
import in.ureport.models.User;
import in.ureport.util.DividerItemDecoration;
import in.ureport.views.adapters.ChatGroupAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatGroupFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ChatGroup>>, ChatGroupAdapter.ChatGroupListener {

    private RecyclerView groupsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private void setupView(View view) {
        groupsList = (RecyclerView) view.findViewById(R.id.groupsList);
        groupsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupsList.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    public Loader<List<ChatGroup>> onCreateLoader(int id, Bundle args) {
        return new ChatGroupsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ChatGroup>> loader, List<ChatGroup> data) {
        ChatGroupAdapter adapter = new ChatGroupAdapter(data);
        adapter.setChatGroupListener(this);
        groupsList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<ChatGroup>> loader) {}

    @Override
    public void onJoinChatGroup(ChatGroup chatGroup) {

    }

    @Override
    public void onViewGroupInfo(ChatGroup chatGroup) {
        Intent groupInfoIntent = new Intent(getActivity(), GroupInfoActivity.class);
        groupInfoIntent.putExtra(GroupInfoActivity.EXTRA_CHAT_GROUP, chatGroup);
        startActivity(groupInfoIntent);
    }
}
