package in.ureport.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import in.ureport.managers.CountryProgramManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.flowrunner.models.Contact;
import in.ureport.models.rapidpro.Field;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

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
        contact.setEmail(user.getEmail());
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

    public Contact buildContactWithFields(User user, Date registrationDate, String countryCode, CountryProgram countryProgram) {
        Contact contact = buildContactWithoutFields(user);
        HashMap<String, Object> contactFields = new HashMap<>();

        String [] possibleStates = countryProgram.getStateField() != null ?
                new String[]{countryProgram.getStateField()} : new String[]{"state", "region", "province", "county"};
        String [] possibleDistricts = countryProgram.getDistrictField() != null ?
                new String[]{countryProgram.getDistrictField()} : new String[]{"location", "district", "lga"};

        putValuesIfExists(user.getNickname(), contactFields, "nickname", "nick_name");
        putValuesIfExists(formatDate(user.getBirthday()), contactFields, "birthday", "birthdate", "birth_day");
        putValuesIfExists(getBornFormatted(user), contactFields, "year_of_birth", "born", "birth_year");
        putValuesIfExists(getAgeFormatted(user), contactFields, "age");
        putValuesIfExists(user.getGender(), contactFields, "gender");
        putValuesIfExists(user.getState(), contactFields, possibleStates);
        putValuesIfExists(user.getDistrict(), contactFields, possibleDistricts);
        putValuesIfExists(countryCode, contactFields, "country");
        putValuesIfExists(formatDate(registrationDate), contactFields, "registration_date", "registrationDate", "registrationdate");

        contact.setFields(contactFields);
        return contact;
    }

    private String formatDate(Date date) {
        if(date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            calendar.setTime(date);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat.format(calendar.getTime());
        }
        return null;
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
    public Integer getBorn(User user) {
        if(user.getBirthday() != null) {
            Calendar calendarBirthday = Calendar.getInstance();
            calendarBirthday.setTime(user.getBirthday());
            return calendarBirthday.get(YEAR);
        }
        return null;
    }

    public String getBornFormatted(User user) {
        Integer born = getBorn(user);
        return born != null ? String.valueOf(born) : null;
    }

    @Nullable
    public static Integer getAge(User user) {
        if(user.getBirthday() != null) {
            Calendar a = Calendar.getInstance();
            a.setTime(user.getBirthday());

            Calendar b = Calendar.getInstance();

            int diff = b.get(YEAR) - a.get(YEAR);
            if (a.get(MONTH) > b.get(MONTH) ||
                    (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
                diff--;
            }
            return diff;
        }
        return null;
    }

    public static String getAgeFormatted(User user) {
        Integer age = getAge(user);
        return age != null ? String.valueOf(age) : null;
    }

}
