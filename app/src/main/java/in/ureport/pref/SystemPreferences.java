package in.ureport.pref;

import android.content.Context;

import br.com.ilhasoft.support.preferences.Preferences;

/**
 * Created by johncordeiro on 7/9/15.
 */
public class SystemPreferences extends Preferences {

    public static final String USER_NO_LOGGED_ID = "NONE";

    private enum Fields {
        UserLoggedId,
        UserLoggedRapidUuid,
        CountryCode,
        CountryToken,
        TutorialView,
        Moderator,
        Master
    }

    public SystemPreferences(Context context) {
        super(context, SystemPreferences.class.getName());
    }

    public void setUserLoggedRapidUuid(String id) {
        setValue(Fields.UserLoggedRapidUuid, id);
    }

    public String getUserLoggedRapidUuid() {
        return getValue(Fields.UserLoggedRapidUuid, USER_NO_LOGGED_ID);
    }

    public void setUserLoggedId(String id) {
        setValue(Fields.UserLoggedId, id);
    }

    public String getUserLoggedId() {
        return getValue(Fields.UserLoggedId, USER_NO_LOGGED_ID);
    }

    public void setCountryCode(String countryCode) {
        setValue(Fields.CountryCode, countryCode);
    }

    public String getCountryCode() {
        return getValue(Fields.CountryCode, "");
    }

    public void setCountryToken(String countryCode) {
        setValue(Fields.CountryToken, countryCode);
    }

    public String getCountryToken() {
        return getValue(Fields.CountryToken, "");
    }

    public void setTutorialView(boolean tutorialView) {
        setValue(Fields.TutorialView, tutorialView);
    }

    public boolean getTutorialView() {
        return getValue(Fields.TutorialView, false);
    }

    public void setModerator(boolean moderator) {
        setValue(Fields.Moderator, moderator);
    }

    public boolean isModerator() {
        return getValue(Fields.Moderator, false);
    }

    public void setMaster(boolean master) {
        setValue(Fields.Master, master);
    }

    public boolean isMaster() {
        return getValue(Fields.Master, false);
    }
}
