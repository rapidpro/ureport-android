package in.ureport.managers;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.ResourceUtil;
import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.models.CountryProgram;
import in.ureport.models.User;

/**
 * Created by johncordeiro on 7/23/15.
 */
public class CountryProgramManager {

    public static final int INVALID_API_TOKEN = -1;
    public static final int INVALID_CHANNEL = -1;
    public static final int INVALID_ORGANIZATION = -1;

    public static final String COUNTRY_PROGRAM_GLOBAL_CODE = "GLOBAL";

    private static CountryProgram countryProgram;
    private static List<CountryProgram> countryPrograms;

    public static void switchCountryProgram(CountryProgram countryProgram) {
        CountryProgramManager.countryProgram = countryProgram;
    }

    public static void switchCountryProgram(String countryCode) {
        CountryProgramManager.countryProgram = getCountryProgramForCode(countryCode);
    }

    public static void switchToUserCountryProgram() {
        CountryProgramManager.countryProgram = getCountryProgramForCode(UserManager.getCountryCode());
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
        return countryProgram != null ? countryProgram : getAvailableCountryPrograms().get(0);
    }

    public static CountryProgram getCountryProgramForCode(String countryCode) {
        CountryProgram countryProgram = new CountryProgram(countryCode);
        int indexOfCountryProgram = getAvailableCountryPrograms().indexOf(countryProgram);
        indexOfCountryProgram = indexOfCountryProgram > 0 ? indexOfCountryProgram : 0;

        return getAvailableCountryPrograms().get(indexOfCountryProgram);
    }

    public static boolean isGlobalUser(User user) {
        return getCountryProgramForCode(user.getCountry()).getCode().equals(COUNTRY_PROGRAM_GLOBAL_CODE);
    }

    public static boolean isCountryProgramEnabled(CountryProgram countryProgram) {
        return countryProgram.getApiToken() != INVALID_API_TOKEN;
    }

    public static boolean allowsPollParticipation() {
        return getCurrentCountryProgram().getChannel() != INVALID_CHANNEL;
    }

    public static List<CountryProgram> getAvailableCountryPrograms() {
        if(countryPrograms == null) {
            countryPrograms = new ArrayList<>();
            countryPrograms.add(buildCountryProgram("GLOBAL", R.style.AppTheme, "U-Report Global", 13, R.string.global_api_token, R.string.global_external_channel, "UReportGlobal", "U-Reporters"));
            countryPrograms.add(buildCountryProgram("BDI", R.style.AppTheme_Burundi, "Burundi", 5, INVALID_API_TOKEN, INVALID_CHANNEL, "UReportGlobal", null));
            countryPrograms.add(buildCountryProgram("CMR", R.style.AppTheme_Cameroun, "Cameroun", 10, INVALID_API_TOKEN, INVALID_CHANNEL, "UReportCameroon", null));
            countryPrograms.add(buildCountryProgram("CHL", R.style.AppTheme_Chile, "Chile", 12, R.string.chile_api_token, R.string.chile_external_channel, "UReportChile", "UReporters"));
            countryPrograms.add(buildCountryProgram("COD", R.style.AppTheme_Drc, "DRC", INVALID_ORGANIZATION, INVALID_API_TOKEN, INVALID_CHANNEL, "UReportDRC", null));
            countryPrograms.add(buildCountryProgram("IDN", R.style.AppTheme_Indonesia, "Indonesia", 15, R.string.indonesia_api_token, R.string.indonesia_external_channel, "UReport_id", "UReporters_Indonesia"));
            countryPrograms.add(buildCountryProgram("LBR", R.style.AppTheme_Liberia, "Liberia", 6, INVALID_API_TOKEN, INVALID_CHANNEL, "UReportLiberia", null));
            countryPrograms.add(buildCountryProgram("MLI", R.style.AppTheme_Mali, "Mali", 3, INVALID_API_TOKEN, INVALID_CHANNEL, "UReportMali", null));
            countryPrograms.add(buildCountryProgram("MEX", R.style.AppTheme_Mexico, "México",  9, INVALID_API_TOKEN, INVALID_CHANNEL, "UreportMexico", null));
            countryPrograms.add(buildCountryProgram("NGA", R.style.AppTheme_Nigeria, "Nigeria", 1, R.string.nigeria_api_token, R.string.nigeria_external_channel, "UReportNigeria", "UReporters"));
            countryPrograms.add(buildCountryProgram("PAK", R.style.AppTheme_Pakistan, "Pakistan", 16, INVALID_API_TOKEN, INVALID_CHANNEL, "PakAvaz", null));
            countryPrograms.add(buildCountryProgram("CAF", R.style.AppTheme_RepubliqueCentrafricaine, "République Centrafricaine", 8, INVALID_API_TOKEN, INVALID_CHANNEL, "Ureport_rca", null));
            countryPrograms.add(buildCountryProgram("SEN", R.style.AppTheme_Senegal, "Sénégal", 14, INVALID_API_TOKEN, INVALID_CHANNEL, "ureportsenegal", null));
            countryPrograms.add(buildCountryProgram("SLE", R.style.AppTheme_SierraLeone, "Sierra Leone", 7, INVALID_API_TOKEN, INVALID_CHANNEL, "UreportSL", null));
            countryPrograms.add(buildCountryProgram("SWZ", R.style.AppTheme_Swaiziland, "Swaziland", 4, INVALID_API_TOKEN, INVALID_CHANNEL, "Ureportszd", null));
            countryPrograms.add(buildCountryProgram("UGA", R.style.AppTheme_Uganda, "Uganda", INVALID_ORGANIZATION, INVALID_API_TOKEN, INVALID_CHANNEL, "UReportUganda", null));
            countryPrograms.add(buildCountryProgram("ZMB", R.style.AppTheme_Zambia, "Zambia", INVALID_ORGANIZATION, INVALID_API_TOKEN, INVALID_CHANNEL, "ZambiaUReport", null));
            countryPrograms.add(buildCountryProgram("ZWE", R.style.AppTheme_Zimbabwe, "Zimbabwe", 2, INVALID_API_TOKEN, INVALID_CHANNEL, "Ureportzim", null));
        }
        return countryPrograms;
    }

    @NonNull
    private static CountryProgram buildCountryProgram(String global, int appTheme, String name, int organization
            , int global_api_token, int global_external_channel, String uReportGlobal, String group) {
        return new CountryProgram(global, appTheme, name, organization, global_api_token, global_external_channel, uReportGlobal, group);
    }

}
