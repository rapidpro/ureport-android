package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.listener.ItemSelectionListener;
import in.ureport.listener.SelectionResultListener;
import in.ureport.loader.UsersLoader;
import in.ureport.models.User;
import in.ureport.views.adapters.CoauthorAdapter;

/**
 * Created by johncordeiro on 7/15/15.
 */
public class InviteCoauthorFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<User>>, ItemSelectionListener<User> {

    private static final String EXTRA_SELECTED_COAUTHORS = "selectedCoauthors";

    private RecyclerView usersList;
    private List<User> selectedCoauthors;

    private SelectionResultListener<User> selectionResultListener;

    public static InviteCoauthorFragment newInstance(ArrayList<User> coauthors) {
        InviteCoauthorFragment inviteCoauthorFragment = new InviteCoauthorFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_SELECTED_COAUTHORS, coauthors);
        inviteCoauthorFragment.setArguments(args);

        return inviteCoauthorFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if(extras != null && extras.containsKey(EXTRA_SELECTED_COAUTHORS)) {
            selectedCoauthors = extras.getParcelableArrayList(EXTRA_SELECTED_COAUTHORS);
        } else {
            selectedCoauthors = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite_coauthor, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_invite_coauthors, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(selectionResultListener != null)
            selectionResultListener.onSelectionResult(selectedCoauthors);
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);

        usersList = (RecyclerView) view.findViewById(R.id.usersList);
        usersList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new UsersLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        CoauthorAdapter coauthorAdapter = new CoauthorAdapter(data, selectedCoauthors);
        coauthorAdapter.setItemSelectionListener(this);

        usersList.setAdapter(coauthorAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {}

    @Override
    public void onItemSelected(User user) {
        selectedCoauthors.add(user);
    }

    @Override
    public void onItemDeselected(User user) {
        selectedCoauthors.remove(user);
    }

    public void setSelectionResultListener(SelectionResultListener<User> selectionResultListener) {
        this.selectionResultListener = selectionResultListener;
    }
}
