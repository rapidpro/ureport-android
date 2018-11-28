package in.ureport.network;

import com.google.firebase.database.DatabaseReference;

import in.ureport.managers.CountryProgramManager;
import in.ureport.managers.FirebaseManager;

/**
 * Created by johncordeiro on 05/09/15.
 */
public abstract class ProgramServices {

    public static final String countryProgramPath = "country_program";

    protected DatabaseReference getCountryProgram() {
        return FirebaseManager.getReference().child(countryProgramPath);
    }

    protected DatabaseReference getDefaultRoot() {
        return FirebaseManager.getReference().child(countryProgramPath)
                .child(CountryProgramManager.getCurrentCountryProgram().getCode());
    }

    protected DatabaseReference getRootByCode(String countryCode) {
        return FirebaseManager.getReference().child(countryProgramPath)
                .child(countryCode);
    }

}
