package in.ureport.managers;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.tool.ResourceUtil;
import br.com.ilhasoft.support.tool.StatusBarDesigner;
import in.ureport.R;
import in.ureport.models.CountryProgram;

/**
 * Created by johncordeiro on 7/23/15.
 */
public class CountryProgramManager {

    public static final int INVALID_VALUE = -1;

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

    public static boolean isPollEnabledForCurrentCountry() {
        return getCurrentCountryProgram().getChannel() != INVALID_VALUE;
    }

    public static boolean isPollEnabledForCountry(CountryProgram countryProgram) {
        return countryProgram.getChannel() != INVALID_VALUE;
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

    public static List<CountryProgram> getAvailableCountryPrograms() {
        if(countryPrograms == null) {
            countryPrograms = new ArrayList<>();
            countryPrograms.add(buildCountryProgram("GLOBAL", R.style.AppTheme, R.string.global_channel, "U-Report Global", 13, "UReportGlobal", "U-Reporters"));
            countryPrograms.add(buildCountryProgram("BDI", R.style.AppTheme_Burundi, INVALID_VALUE, "Burundi", 5, "UReportGlobal", null));
            countryPrograms.add(buildCountryProgram("CMR", R.style.AppTheme_Cameroun, INVALID_VALUE, "Cameroun", 10, "UReportCameroon", null));
            countryPrograms.add(buildCountryProgram("CHL", R.style.AppTheme_Chile, R.string.chile_channel, "Chile", 12, "ureportchile", "UReporters"));
            countryPrograms.add(buildCountryProgram("COD", R.style.AppTheme_Drc, INVALID_VALUE, "DRC", INVALID_VALUE, "UReportDRC", null));
            countryPrograms.add(buildCountryProgram("IDN", R.style.AppTheme_Indonesia, R.string.indonesia_channel, "Indonesia", 15, "UReport_id", "UReporters_Indonesia"));
            countryPrograms.add(buildCountryProgram("LBR", R.style.AppTheme_Liberia, INVALID_VALUE, "Liberia", 6, "UReportLiberia", null));
            countryPrograms.add(buildCountryProgram("MLI", R.style.AppTheme_Mali, INVALID_VALUE, "Mali", 3, "UReportMali", null));
            countryPrograms.add(buildCountryProgram("MEX", R.style.AppTheme_Mexico, R.string.mexico_channel, "México",  9, "UReportMexico", "UReporters"));
//            countryPrograms.add(buildCountryProgram("MMR", R.style.AppTheme_Myanmar, INVALID_VALUE, "Myanmar",  INVALID_VALUE, "UReportMyanmar", "UReporters"));
            countryPrograms.add(buildCountryProgram("NGA", R.style.AppTheme_Nigeria, R.string.nigeria_channel, "Nigeria", 1, "UReportNigeria", "UReporters"));
            countryPrograms.add(buildCountryProgram("PAK", R.style.AppTheme_Pakistan, INVALID_VALUE, "Pakistan", 16, "PakAvaz", null));
            countryPrograms.add(buildCountryProgram("CAF", R.style.AppTheme_RepubliqueCentrafricaine, INVALID_VALUE, "République Centrafricaine", 8, "Ureport_rca", null));
            countryPrograms.add(buildCountryProgram("SEN", R.style.AppTheme_Senegal, INVALID_VALUE, "Sénégal", 14, "ureportsenegal", null));
            countryPrograms.add(buildCountryProgram("SLE", R.style.AppTheme_SierraLeone, INVALID_VALUE, "Sierra Leone", 7, "UreportSL", null));
            countryPrograms.add(buildCountryProgram("SWZ", R.style.AppTheme_Swaiziland, INVALID_VALUE, "Swaziland", 4, "Ureportszd", null));
            countryPrograms.add(buildCountryProgram("UGA", R.style.AppTheme_Uganda, R.string.uganda_channel, "Uganda", 18, "UReportUganda", "U-Reporters"));
            countryPrograms.add(buildCountryProgram("UKR", R.style.AppTheme_Ukraine, R.string.ukraine_channel, "Ukraine", 19, "ureportukraine", "UReporters"));
            countryPrograms.add(buildCountryProgram("ZMB", R.style.AppTheme_Zambia, INVALID_VALUE, "Zambia", INVALID_VALUE, "ZambiaUReport", null));
            countryPrograms.add(buildCountryProgram("ZWE", R.style.AppTheme_Zimbabwe, INVALID_VALUE, "Zimbabwe", 2, "Ureportzim", null));
        }
        return countryPrograms;
    }

    @NonNull
    private static CountryProgram buildCountryProgram(String global, int appTheme, int channel, String name, int organization
            , String twitter, String group) {
        return new CountryProgram(global, appTheme, channel, name, organization, twitter, group);
    }

}
