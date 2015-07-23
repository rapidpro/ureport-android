package in.ureport.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import br.com.ilhasoft.support.tool.EditTextValidator;
import in.ureport.R;
import in.ureport.listener.ChatCreationListener;
import in.ureport.loader.UreportersLoader;
import in.ureport.models.User;
import in.ureport.views.adapters.UreportersAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class CreateGroupFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<User>> {

    public static final int MIN_SIZE_TITLE = 5;
    private EditText title;
    private EditText description;
    private RecyclerView ureportersList;

    private EditTextValidator validator;

    private ChatCreationListener chatCreationListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private void setupObjects() {
        validator = new EditTextValidator();
    }

    private void setupView(View view) {
        setHasOptionsMenu(true);

        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);

        ureportersList = (RecyclerView) view.findViewById(R.id.ureportersList);
        ureportersList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.label_create_group);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_new_group, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createGroup:
                createGroup();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createGroup() {
        if(validateFields()) {
            showSuccessAlert();
        }
    }

    private void showSuccessAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.chat_create_result_title)
                .setMessage(R.string.chat_create_group_result_message)
                .setNeutralButton(R.string.confirm_neutral_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (chatCreationListener != null)
                            chatCreationListener.onChatRoomCreated();
                    }
                }).create();
        alertDialog.show();
    }

    private boolean validateFields() {
        boolean titleValid = validator.validateSize(title, MIN_SIZE_TITLE, getString(R.string.error_minimum_size, MIN_SIZE_TITLE));
        boolean descriptionValid = validator.validateEmpty(description, getString(R.string.error_required_field));

        return titleValid && descriptionValid;
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new UreportersLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        UreportersAdapter ureportersAdapter = new UreportersAdapter(data);
        ureportersAdapter.setSelectionEnabled(true);
        ureportersList.setAdapter(ureportersAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {}

    public void setChatCreationListener(ChatCreationListener chatCreationListener) {
        this.chatCreationListener = chatCreationListener;
    }
}
