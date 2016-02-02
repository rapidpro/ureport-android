package in.ureport.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ureport.models.User;
import in.ureport.flowrunner.models.Contact;
import in.ureport.models.geonames.CountryInfo;
import in.ureport.models.rapidpro.Field;

/**
 * Created by johncordeiro on 24/09/15.
 */
public class ContactBuilder {

    private static final String URL_FORMAT = "ext:%1$s";

    private List<Field> existingFields;

    public ContactBuilder(List<Field> existingFields) {
        this.existingFields = existingFields;
    }

    public ContactBuilder() {
    }

    @NonNull
    public Contact buildContactWithoutFields(User user) {
        ContactGroupsBuilder contactGroupsBuilder = new ContactGroupsBuilder();
        List<String> userGroups = contactGroupsBuilder.getGroupsForUser(user);

        List<String> urns = new ArrayList<>();
        urns.add(formatExtUrn(user.getKey()));

        Contact contact = new Contact();
        contact.setName(user.getNickname());
        contact.setGroups(userGroups);
        contact.setUrns(urns);
        return contact;
    }

    public String formatExtUrn(String key) {
        return String.format(URL_FORMAT, formatUserId(key));
    }

    public String formatUserId(String key) {
        return key.replace(":", "").replace("-", "");
    }

    public Contact buildContactWithFields(User user, String countryCode) {
        Contact contact = buildContactWithoutFields(user);
        HashMap<String, Object> contactFields = new HashMap<>();

        putValuesIfExists(user.getEmail(), contactFields, "email", "e_mail");
        putValuesIfExists(user.getNickname(), contactFields, "nickname", "nick_name");
        putValuesIfExists(user.getBirthday(), contactFields, "birthday", "birthdate", "birth_day");
        putValuesIfExists(getBorn(user), contactFields, "born");
        putValuesIfExists(user.getGender().toString(), contactFields, "gender");
        putValuesIfExists(user.getState(), contactFields, "state", "region", "province", "county");
        putValuesIfExists(user.getDistrict(), contactFields, "district", "lga");
        putValuesIfExists(countryCode, contactFields, "country");

        contact.setFields(contactFields);
        return contact;
    }

    private void putValuesIfExists(Object value, Map<String, Object> contactFields, String... possibleKeys) {
        for (String possibleField : possibleKeys) {
            int indexOfField = existingFields.indexOf(new Field(possibleField));
            if(indexOfField >= 0 && value != null) {
                contactFields.put(possibleField, value);
                break;
            }
        }
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
