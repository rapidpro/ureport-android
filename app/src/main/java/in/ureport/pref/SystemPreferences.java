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
        CountryCode,
        FakeDateCreated
    }

    public SystemPreferences(Context context) {
        super(context, SystemPreferences.class.getName());
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

    public void setFakeDataCreated(Boolean created) {
        setValue(Fields.FakeDateCreated, created);
    }

    public Boolean isFakeDataCreated() {
        return getValue(Fields.FakeDateCreated, false);
    }
}
