package in.ureport.network;

import com.firebase.client.Firebase;

import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseManager;
import in.ureport.managers.UserManager;

/**
 * Created by johncordeiro on 05/09/15.
 */
public abstract class ProgramServices {

    public static final String countryProgramPath = "country_program";

    protected Firebase getDefaultRoot() {
        return FirebaseManager.getReference().child(countryProgramPath)
                .child(CountryProgramManager.getCurrentCountryProgram().getCode());
    }

}
