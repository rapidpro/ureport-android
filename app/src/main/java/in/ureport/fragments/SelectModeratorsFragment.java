package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

import in.ureport.R;
import in.ureport.listener.ItemSelectionListener;
import in.ureport.managers.SearchManager;
import in.ureport.models.User;
import in.ureport.network.UserServices;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 17/09/15.
 */
public class SelectModeratorsFragment extends Fragment implements SearchView.OnCloseListener
        , SearchView.OnQueryTextListener, ItemSelectionListener<User> {

    private UserServices userServices;
    private RecyclerView ureportersList;

    private UreportersAdapter ureportersAdapter;

    private ValueEventListener userEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_moderators, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObjects();
        setupView(view);
        loadMasterModerators();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(userEventListener != null) userServices.removeCountryCodeListener(userEventListener);
    }

    private void setupView(View view) {
        ureportersList = (RecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setupObjects() {
        userServices = new UserServices();
    }

    private void loadMasterModerators() {
        userServices.loadMasterModerators(new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(List<User> masterModerators) {
                loadCountryModerators(masterModerators);
            }
        });
    }

    private void loadCountryModerators(final List<User> masterModerators) {
        userServices.loadCountryModerators(new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(final List<User> countryModerators) {
                loadUsersByCountryCode(masterModerators, countryModerators);
            }
        });
    }

    private void loadUsersByCountryCode(final List<User> masterModerators, final List<User> countryModerators) {
        userEventListener = userServices.loadByCountryCode(new UserServices.OnLoadAllUsersListener() {
            @Override
            public void onLoadAllUsers(List<User> users) {
                users.removeAll(masterModerators);
                setupAdapter(users, countryModerators);
            }
        });
    }

    private void setupAdapter(List<User> users, List<User> moderators) {
        ureportersAdapter = new UreportersAdapter(users);
        ureportersAdapter.setSelectionEnabled(true, null, moderators);
        ureportersAdapter.setItemSelectionListener(this);
        ureportersList.setAdapter(ureportersAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = new SearchManager(getActivity());
        searchManager.addSearchView(menu, R.string.search_hint_users, this, this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ureportersAdapter.search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ureportersAdapter.search(newText);
        return false;
    }

    @Override
    public boolean onClose() {
        ureportersAdapter.clearSearch();
        return false;
    }

    @Override
    public void onItemSelected(User item) {
        userServices.addCountryModerator(item, onUpdateUserCompletionListener);
    }

    @Override
    public void onItemDeselected(User item) {
        userServices.removeCountryModerator(item, onUpdateUserCompletionListener);
    }

    private void displayToast(@StringRes int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private Firebase.CompletionListener onUpdateUserCompletionListener = new Firebase.CompletionListener() {
        @Override
        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            if (firebaseError == null) {
                displayToast(R.string.message_success_user_update);
            } else {
                displayToast(R.string.error_update_user);
            }
        }
    };
}
