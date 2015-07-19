package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.ureport.R;
import in.ureport.listener.ChatCreationListener;
import in.ureport.loader.UreportersLoader;
import in.ureport.models.User;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class NewChatFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<User>>{

    private RecyclerView ureportersList;

    private ChatCreationListener chatCreationListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private void setupView(View view) {
        TextView createGroup = (TextView) view.findViewById(R.id.createGroup);
        createGroup.setOnClickListener(onCreateGroupClickListener);

        ureportersList = (RecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.label_new_chat);
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new UreportersLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        UreportersAdapter adapter = new UreportersAdapter(data);
        adapter.setChatCreationListener(chatCreationListener);
        ureportersList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {}

    public void setChatCreationListener(ChatCreationListener chatCreationListener) {
        this.chatCreationListener = chatCreationListener;
    }

    private View.OnClickListener onCreateGroupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (chatCreationListener != null)
                chatCreationListener.onCreateGroupChatCalled();
        }
    };
}
