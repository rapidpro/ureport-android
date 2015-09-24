package in.ureport.tasks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ureport.R;
import in.ureport.helpers.UserGroupsBuilder;
import in.ureport.managers.CountryProgramManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.models.rapidpro.Contact;
import in.ureport.models.rapidpro.ContactFields;
import in.ureport.network.RapidProServices;
import in.ureport.tasks.common.ProgressTask;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class SaveContactTask extends ProgressTask<User, Void, Contact> {

    private static final String TAG = "SaveContactTask";

    public SaveContactTask(Context context) {
        super(context, R.string.load_message_save_user);
    }

    @Override
    protected Contact doInBackground(User... params) {
        try {
            User user = params[0];

            CountryProgram countryProgram = CountryProgramManager.getCountryProgramForCode(user.getCountry());
            Contact contact = buildContact(user);

            if(CountryProgramManager.isCountryProgramEnabled(countryProgram)) {
                RapidProServices rapidProServices = new RapidProServices();
                String token = context.getString(countryProgram.getApiToken());
                try {
                    return rapidProServices.saveContact(token, contact);
                } catch(RetrofitError exception) {
                    contact.getFields().setState(null);
                }
                return rapidProServices.saveContact(token, contact);
            } else {
                return contact;
            }
        } catch (Exception exception) {
            Log.e(TAG, "doInBackground ", exception);
        }
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
        fields.setNickname(user.getNickname());
        fields.setBirthday(user.getBirthday());
        fields.setBorn(getBorn(user));
        fields.setEmail(user.getEmail());
        fields.setGender(user.getGender().toString());
        fields.setState(user.getState());
        contact.setFields(fields);

        return contact;
    }

    @Nullable
    private String getBorn(User user) {
        if(user.getBirthday() != null) {
            Calendar calendarBirthday = Calendar.getInstance();
            calendarBirthday.setTime(user.getBirthday());
            return String.valueOf(calendarBirthday.get(Calendar.YEAR));
        }
        return null;
    }

}
