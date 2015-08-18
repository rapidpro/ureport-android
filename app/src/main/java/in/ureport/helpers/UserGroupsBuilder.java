package in.ureport.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ureport.models.User;
import in.ureport.models.rapidpro.Group;

/**
 * Created by johncordeiro on 18/08/15.
 */
public class UserGroupsBuilder {

    private static final int YOUTH_MIN_BIRTHDAY_YEAR = 1979;

    private static final String GROUP_UREPORTERS = "UReporters";
    private static final String GROUP_UREPORT_YOUTH = "UReport Youth";
    private static final String GROUP_UREPORT_ADULTS = "UReport Adults";
    private static final String GROUP_UREPORT_MALES = "UReport Males";
    private static final String GROUP_UREPORT_FEMALES = "UReport Females";

    public List<String> getGroupsForUser(User user) {
        List<String> userGroups = new ArrayList<>();
        userGroups.add(GROUP_UREPORTERS);
        addGenderGroup(user, userGroups);
        addAgeGroup(user, userGroups);
        return userGroups;
    }

    private void addAgeGroup(User user, List<String> userGroups) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getBirthday());

        if(calendar.get(Calendar.YEAR) >= YOUTH_MIN_BIRTHDAY_YEAR) {
            userGroups.add(GROUP_UREPORT_YOUTH);
        } else {
            userGroups.add(GROUP_UREPORT_ADULTS);
        }
    }

    private void addGenderGroup(User user, List<String> userGroups) {
        if(user.getGender() == User.Gender.Male) {
            userGroups.add(GROUP_UREPORT_MALES);
        } else if(user.getGender() == User.Gender.Female) {
            userGroups.add(GROUP_UREPORT_FEMALES);
        }
    }
}
