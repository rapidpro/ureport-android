package in.ureport.pref;

import android.content.Context;

import br.com.ilhasoft.support.preferences.Preferences;

/**
 * Created by ilhasoft on 7/9/15.
 */
public class SystemPreferences extends Preferences {

    public static final Long USER_NO_LOGGED_ID = -1L;

    private enum Fields {
        UserLoggedId
    }

    public SystemPreferences(Context context) {
        super(context, SystemPreferences.class.getName());
    }

    public void setUserLoggedId(Long id) {
        setValue(Fields.UserLoggedId, id);
    }

    public Long getUserLoggedId() {
        return getValue(Fields.UserLoggedId, USER_NO_LOGGED_ID);
    }
}
