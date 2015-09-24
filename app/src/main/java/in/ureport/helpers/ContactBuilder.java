package in.ureport.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import in.ureport.managers.CountryProgramManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.models.rapidpro.Contact;
import in.ureport.models.rapidpro.ContactFields;

/**
 * Created by johncordeiro on 24/09/15.
 */
public class ContactBuilder {

    private static final String URL_FORMAT = "tel:%1$s";

    @NonNull
    public Contact buildContactByUser(User user, Locale locale) {
        ContactGroupsBuilder contactGroupsBuilder = new ContactGroupsBuilder();
        List<String> userGroups = contactGroupsBuilder.getGroupsForUser(user);

        List<String> urns = new ArrayList<>();
        urns.add(String.format(URL_FORMAT, user.getKey()));

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
        fields.setLga(user.getDistrict());

        if(CountryProgramManager.isGlobalUser(user)) {
            fields.setCountry(locale.getCountry());
        }

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
