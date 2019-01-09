package in.ureport.managers;

import android.app.Activity;
import android.support.annotation.NonNull;

import br.com.ilhasoft.support.tool.ResourceUtil;
import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.models.CountryProgram;

import static in.ureport.R.string.rapidpro_host_address1;
import static in.ureport.R.string.ureport_host_address1;
import static in.ureport.R.style.AppTheme_Otm;

/**
 * Created by johncordeiro on 7/23/15.
 */
public class CountryProgramManager {

    public static final int INVALID_VALUE = -1;
    private static CountryProgram countryProgram;

    public static void switchCountryProgram(String countryCode) {
        CountryProgramManager.countryProgram = getCountryProgramForCode(countryCode);
    }

    public static void switchToUserCountryProgram() {
        CountryProgramManager.countryProgram = getCountryProgramForCode(UserManager.getCountryCode());
    }

    public static CountryProgram getCountryProgramForCode(String countryCode) {
        return buildCountryProgramIfNeeded();
    }

    public static void setThemeIfNeeded(Activity activity) {
        CountryProgram countryProgram = getCurrentCountryProgram();
        activity.setTheme(countryProgram.getTheme());

        ResourceUtil resourceUtil = new ResourceUtil(activity);
        StatusBarDesigner statusBarDesigner = new StatusBarDesigner();
        statusBarDesigner.setStatusBarColor(activity, resourceUtil.getColorByAttr(R.attr.colorPrimaryDark));
    }

    @NonNull
    public static CountryProgram getCurrentCountryProgram() {
        return buildCountryProgramIfNeeded();
    }

    @NonNull
    private static CountryProgram buildCountryProgramIfNeeded() {
        if (countryProgram == null) {
            countryProgram = new CountryProgram("OTM", AppTheme_Otm, R.string.otm_channel, "On The Move", 33
                    , rapidpro_host_address1, ureport_host_address1, "UreportOTM", "UreportOnTheMove", "U-Reporters");
            countryProgram.setMaleGroup("U-Report Males");
            countryProgram.setFemaleGroup("U-Report Females");
            countryProgram.setStateField("update_value_for_region");
            countryProgram.setDistrictField("province");
        }
        return countryProgram;
    }

}
