package in.ureport.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.ureport.R;
import in.ureport.models.Contact;
import in.ureport.views.adapters.ContactsAdapter;

/**
 * Created by johncordeiro on 19/07/15.
 */
public class InviteContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
    , ContactsAdapter.OnContactInvitedListener{

    private static final String[] PROJECTION = {Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER};
    public static final int REQUEST_CODE_PERMISSION_CONTACTS = 1943;
    private RecyclerView contactsList;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getContext()
                , Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_PERMISSION_CONTACTS);
        } else {
            loadData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && allPermissionsGranted(grantResults)) {
            loadData();
        } else {
            Toast.makeText(getContext(), R.string.error_message_permission_external, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        boolean granted = true;
        for (int grantResult : grantResults) {
            if(grantResult != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        return granted;
    }

    private void loadData() {
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Phone.CONTENT_URI, PROJECTION, null, null
                , Phone.DISPLAY_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Contact> contacts = getContactsFromCursor(data);

        ContactsAdapter adapter = new ContactsAdapter(contacts);
        adapter.setOnContactInvitedListener(this);
        contactsList.setAdapter(adapter);
    }

    @NonNull
    private List<Contact> getContactsFromCursor(Cursor data) {
        List<Contact> contacts = new ArrayList<>();
        int displayNameIndex = data.getColumnIndex(Phone.DISPLAY_NAME);
        int numberIndex = data.getColumnIndex(Phone.NUMBER);
        while(data.moveToNext()) {
            contacts.add(new Contact(data.getString(displayNameIndex), data.getString(numberIndex)));
        }
        return contacts;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onContactInvited(Contact contact) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + contact.getPhoneNumber()));
        sendIntent.putExtra("sms_body", getString(R.string.invite_contact_text));
        startActivityForResult(sendIntent, 0);
    }
}
