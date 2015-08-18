package in.ureport.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.UserGroupsBuilder;
import in.ureport.models.User;
import in.ureport.models.rapidpro.Contact;
import in.ureport.models.rapidpro.ContactFields;
import in.ureport.models.rapidpro.Group;
import in.ureport.network.RapidProServices;
import in.ureport.tasks.common.ProgressTask;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class SaveContactTask extends ProgressTask<User, Void, Void> {

    private static final String TAG = "SaveContactTask";

    public SaveContactTask(Context context) {
        super(context, R.string.load_message_save_user);
    }

    @Override
    protected Void doInBackground(User... params) {
        if(params.length == 0) return null;
        User user = params[0];

        RapidProServices rapidProServices = new RapidProServices();
        Contact contact = buildContact(user);

        Contact contactResponse = rapidProServices.saveContact(contact);
        Log.i(TAG, "doInBackground " + contactResponse);

        return null;
    }

    @NonNull
    private Contact buildContact(User user) {
        UserGroupsBuilder userGroupsBuilder = new UserGroupsBuilder();
        List<String> userGroups = userGroupsBuilder.getGroupsForUser(user);

        List<String> urns = new ArrayList<>();
        urns.add(String.format("tel:%1$s", user.getKey()));

        Contact contact = new Contact();
        contact.setName(user.getNickname());
        contact.setGroups(userGroups);
        contact.setUrns(urns);

        ContactFields fields = new ContactFields();
        fields.setNick_name(user.getNickname());
        fields.setBirthday(user.getBirthday());
        fields.setEmail(user.getEmail());
        fields.setGender(user.getGender().toString());
        fields.setState(user.getState());
        contact.setFields(fields);

        return contact;
    }

}
