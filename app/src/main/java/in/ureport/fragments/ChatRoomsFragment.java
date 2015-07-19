package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.ureport.R;
import in.ureport.loader.ChatRoomsLoader;
import in.ureport.models.ChatRoom;
import in.ureport.util.DividerItemDecoration;
import in.ureport.views.adapters.ChatRoomsAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class ChatRoomsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ChatRoom>> {

    private RecyclerView chatsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_rooms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private void setupView(View view) {
        chatsList = (RecyclerView) view.findViewById(R.id.chatsList);
        chatsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatsList.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    public Loader<List<ChatRoom>> onCreateLoader(int id, Bundle args) {
        return new ChatRoomsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ChatRoom>> loader, List<ChatRoom> data) {
        ChatRoomsAdapter chatRoomsAdapter = new ChatRoomsAdapter(data);
        chatsList.setAdapter(chatRoomsAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<ChatRoom>> loader) {}
}
