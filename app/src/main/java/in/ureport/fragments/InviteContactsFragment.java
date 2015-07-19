package in.ureport.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.models.Contact;
import in.ureport.views.adapters.ContactsAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class InviteContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION = {ContactsContract.Contacts._ID,
                                                ContactsContract.Contacts.LOOKUP_KEY,
                                                getDisplayName()};
    private RecyclerView contactsList;

    @NonNull
    @SuppressLint("InlinedApi")
    private static String getDisplayName() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                ContactsContract.Contacts.DISPLAY_NAME;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactsList = (RecyclerView) view.findViewById(R.id.contactsList);
        contactsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null
                , getDisplayName() + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Contact> contacts = getContactsFromCursor(data);

        ContactsAdapter adapter = new ContactsAdapter(contacts);
        contactsList.setAdapter(adapter);
    }

    @NonNull
    private List<Contact> getContactsFromCursor(Cursor data) {
        List<Contact> contacts = new ArrayList<>();
        int displayNameIndex = data.getColumnIndex(getDisplayName());
        while(data.moveToNext()) {
            contacts.add(new Contact(data.getString(displayNameIndex)));
        }
        return contacts;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
