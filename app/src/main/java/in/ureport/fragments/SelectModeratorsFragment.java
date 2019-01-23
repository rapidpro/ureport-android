package in.ureport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.marcorei.infinitefire.InfiniteFireArray;

import java.util.List;

import in.ureport.R;
import in.ureport.listener.ItemSelectionListener;
import in.ureport.models.User;
import in.ureport.network.UserServices;
import in.ureport.views.widgets.InfiniteFireLinearRecyclerView;
import in.ureport.views.adapters.UreportersInfiniteAdapter;

/**
 * Created by johncordeiro on 17/09/15.
 */
public class SelectModeratorsFragment extends Fragment implements ItemSelectionListener<User> {

    private UserServices userServices;

    private InfiniteFireLinearRecyclerView ureportersList;

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
        loadCountryModerators();
    }

    private void setupView(View view) {
        ureportersList = (InfiniteFireLinearRecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setupObjects() {
        userServices = new UserServices();
    }

    private void loadCountryModerators() {
        userServices.loadCountryModerators(this::loadUsers);
    }

    private void loadUsers(List<User> moderators) {
        InfiniteFireArray<User> usersFireArray = new InfiniteFireArray<>(User.class
                , userServices.getUserCountryProgramQuery(), 30, 30, false, false);

        UreportersInfiniteAdapter ureportersAdapter = new UreportersInfiniteAdapter(usersFireArray);
        ureportersAdapter.setHasStableIds(true);
        ureportersAdapter.setSelectionEnabled(true, null, moderators);
        ureportersAdapter.setItemSelectionListener(this);

        ureportersList.setAdapter(ureportersAdapter);
        ureportersList.setInfiniteFireArray(usersFireArray);
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

    private DatabaseReference.CompletionListener onUpdateUserCompletionListener = (error, reference) -> {
        if (error == null) {
            displayToast(R.string.message_success_user_update);
        } else {
            displayToast(R.string.error_update_user);
        }
    };
}
