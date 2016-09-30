package in.ureport.helpers;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ureport.managers.CountryProgramManager;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;
import in.ureport.models.rapidpro.AgeGroup;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class ContactGroupsBuilder {

    private static final String TAG = "ContactGroupsBuilder";

    private static final int YOUTH_MIN_BIRTHDAY_YEAR = 1979;

    private static final String GROUP_UREPORT_YOUTH = "UReport Youth";
    private static final String GROUP_UREPORT_ADULTS = "UReport Adults";
    private static final String GROUP_UREPORT_APP = "App U-Reporters";

    public List<String> getGroupsForUser(User user) {
        List<String> userGroups = new ArrayList<>();
        CountryProgram countryProgram = CountryProgramManager.getCountryProgramForCode(user.getCountry());
        if(countryProgram.getGroup() != null) {
            userGroups.add(countryProgram.getGroup());
        }
        userGroups.add(GROUP_UREPORT_APP);
        addGenderGroup(user, countryProgram, userGroups);
        addAgeGroup(user, countryProgram, userGroups);
        return userGroups;
    }

    private void addAgeGroup(User user, CountryProgram countryProgram, List<String> userGroups) {
        if (countryProgram.getAgeGroups() != null && !countryProgram.getAgeGroups().isEmpty()) {
            addCountryGroups(user, countryProgram, userGroups);
        } else {
            addDefaultGroups(user, userGroups);
        }

    }

    private void addCountryGroups(User user, CountryProgram countryProgram, List<String> userGroups) {
        Integer age = ContactBuilder.getAge(user);
        for (AgeGroup ageGroup : countryProgram.getAgeGroups()) {
            try {
                if (age >= ageGroup.getMinAge() && age <= ageGroup.getMaxAge()) {
                    userGroups.add(ageGroup.getGroup());
                }
            } catch(Exception exception) {
                Log.e(TAG, "addAgeGroup: ", exception);
            }
        }
    }

    private void addDefaultGroups(User user, List<String> userGroups) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getBirthday());

        if(calendar.get(Calendar.YEAR) >= YOUTH_MIN_BIRTHDAY_YEAR) {
            userGroups.add(GROUP_UREPORT_YOUTH);
        } else {
            userGroups.add(GROUP_UREPORT_ADULTS);
        }
    }

    private void addGenderGroup(User user, CountryProgram countryProgram, List<String> userGroups) {
        if(user.getGender() == User.Gender.Male) {
            userGroups.add(countryProgram.getMaleGroup());
        } else if(user.getGender() == User.Gender.Female) {
            userGroups.add(countryProgram.getFemaleGroup());
        }
    }
}
